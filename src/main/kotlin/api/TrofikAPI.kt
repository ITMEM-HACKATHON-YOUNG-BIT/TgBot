package api

import de.jensklingenberg.ktorfit.http.*
import domain.*

interface TrofikAPI {
    @GET("users/notification")
    @Headers("Content-Type: application/json")
    suspend fun usersForNotification(@QueryName delay: Int): Notification
    @POST("user/create/tg")
    @Headers("Content-Type: application/json")
    suspend fun newUser(@Body user: User): UserInfo
    @HTTP("GET", "user/message", hasBody = true)
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(@Body message: UserMessage): TrofikAnswer
}