import api.trofikAPI
import dev.inmo.micro_utils.coroutines.safely
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onVoice
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.caption
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.utils.*
import domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*


@OptIn(RiskFeature::class)
suspend fun main() {
    val bot = telegramBot(TG_TOKEN)

    bot.buildBehaviourWithLongPolling(defaultExceptionsHandler = {
        println("Error:")
        println(it.message)
        println(it.cause)
        it.printStackTrace()
        println("____________________")
    }, scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()) {
        println(getMe())

        launch {
            notificationHandler()
        }

        onVoice { message ->
            println("Voice message from ${message.from?.username}")
            trofikAPI.sendVoiceMessage(bot.downloadFile(message.content.media.fileId)).let {
                trofikAnswerCallback(it, message.chat.id.chatId)
            }
        }

        onContentMessage { message ->
            println("Send message to trofik $message")
            if (message.text?.startsWith("/") != false) return@onContentMessage
            trofikAPI.sendMessage(
                UserMessage(
                    message.chat.id.chatId,
                    message.text ?: message.caption ?: "Не удалось получить текст сообщения"
                ).also(::println)
            ).let {
                trofikAnswerCallback(it, message.chat.id.chatId, message)
            }
        }

        onCommand("start") {
            suspend fun getUserName() = "@" + waitText(
                SendTextMessage(
                    it.chat.id,
                    "Не удалось увидеть ваш username. Пожалуйста, введите его:",
                    replyMarkup = ReplyKeyboardMarkup()
                )
            ).first().text.removePrefix("@")

            suspend fun getUser() = User(it.chat.id.chatId, getUserName())
            println("New user")
            val user = it.from?.let {
                User(it.id.chatId, it.username?.username ?: getUserName())
            } ?: getUser()
            val userInfo = safely({ null }) {
                trofikAPI.newUser(user)
            }

            fun greetingText(userInfo: UserInfo?) = if (userInfo != null) {
                "Привет, ${userInfo.firstName}! Теперь ты можешь получать уведомления о новых мероприятиях и изменениях!"
            } else {
                "К сожалению, не удалось найти тебя в базе. Попробуй заново привязать тг аккаунт на сайте."
            }
            execute(
                SendTextMessage(
                    it.chat.id, greetingText(userInfo)
                )
            )
            println(user)
        }
    }.join()
}

@OptIn(RiskFeature::class)
private suspend fun BehaviourContext.trofikAnswerCallback(
    it: TrofikAnswer,
    chatId: Long,
    message: CommonMessage<MessageContent>? = null
) {
    it.message?.let {
        execute(
            SendTextMessage(
                ChatId(chatId), it
            )
        )
    } ?: it.adminChatID?.let { id ->
        execute(
            SendTextMessage(
                ChatId(id),
                "У ${it.username} возник следующий вопрос, на который бот не смог ответить: ${message?.text ?: it.question}",
            )
        )
        execute(
            SendTextMessage(
                ChatId(chatId),
                "Ваш вопрос был отправлен администратору. Ожидайте ответа."
            )
        )
    }
}

private suspend fun BehaviourContext.notificationHandler() {
    suspend fun notify(notification: Notification) {
        println(notification)
        notification.users.forEach { user ->
            safely {
                execute(
                    SendTextMessage(
                        ChatId(user.tgUserID), notification.message.replace(
                            "<name>", user.firstName ?: user.username ?: "пользователь"
                        )
                    )
                )
            }
        }
    }
    while (true) {
        println("Notification check")
        safely({ null }) {
            trofikAPI.usersForNotification(10000000).forEach { notify(it) }
        }
        delay(NOTIFICATION_DELAY * 1000L)
    }
}