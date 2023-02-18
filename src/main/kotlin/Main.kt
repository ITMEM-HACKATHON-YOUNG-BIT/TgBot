import api.trofikAPI
import dev.inmo.micro_utils.coroutines.safely
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.caption
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.utils.*
import domain.Notification
import domain.User
import domain.UserMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*


@OptIn(RiskFeature::class)
suspend fun main() {
    val bot = telegramBot(TG_TOKEN)

    bot.buildBehaviourWithLongPolling {
        println(getMe())

        launch {
            suspend fun notify(notification: Notification) {
                println(notification)
                notification.users.forEach { user ->
                    safely {
                        execute(
                            SendTextMessage(
                                ChatId(user.tgUserID),
                                notification.message.replace("{name}", user.firstName ?: user.username!!)
                            )
                        )
                    }
                }
            }
            while (true) {
                delay(NOTIFICATION_DELAY * 1000L)
                notify(trofikAPI.usersForNotification(NOTIFICATION_DELAY))
            }
        }

        onContentMessage { message ->
            trofikAPI.sendMessage(
                UserMessage(
                    message.chat.id.chatId, message.text ?: message.caption ?: "Не удалось получить текст сообщения"
                ).also(::println)
            ).let {
                execute(
                    SendTextMessage(
                        message.chat.id, it.message
                    )
                )
            }
        }

        onCommand("start") {
            suspend fun getUserName() = "@" + waitText(
                SendTextMessage(
                    it.chat.id,
                    "Не удалось увидеть ваш username. Пожалуйста, введите его:",
                )
            ).first().text.removePrefix("@")

            suspend fun getUser() = User(it.chat.id.chatId, getUserName())
            println("New user")
            val user = it.from?.let {
                User(it.id.chatId, it.username?.username ?: getUserName())
            } ?: getUser()
            execute(
                SendTextMessage(
                    it.chat.id,
                    "Привет, ${trofikAPI.newUser(user)}! Теперь ты можешь получать уведомления о новых мероприятиях и изменениях!",
                    replyMarkup = ReplyKeyboardMarkup()
                )
            )
            println(user)
        }
    }.join()
}