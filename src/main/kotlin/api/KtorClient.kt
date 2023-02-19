package api

import BASE_URL
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val ktorcl = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}
val ktorfit = Ktorfit.Builder().httpClient(ktorcl).baseUrl(BASE_URL).build()
val trofikAPI = ktorfit.create<TrofikAPI>()

//val trofikAPI = object : TrofikAPI {
//    override suspend fun usersForNotification(delay: Int) =
//        Notification(
//            listOf(
//                User(398108733, "@noisegain", "Илюшка"),
//                User(tgUserID=664892538, username="@trofik00777", firstName="Максик"),
//                User(tgUserID=619363332, username="@DenchicEz", firstName="Дениска-пиписка"),
//            ),
//            "Это тестовая рассылка специально для {name}"
//        )
//
//    override suspend fun newUser(user: User) = UserInfo("Илья", "Пономаренко")
//}