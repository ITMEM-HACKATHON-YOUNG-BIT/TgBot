package api

import de.jensklingenberg.ktorfit.http.*
import domain.*

interface TrofikAPI {
    @GET("events/get_by_delay")
    @Headers("Content-Type: application/json")
    suspend fun usersForNotification(@Query("delay") delay: Int): List<Notification>
    @POST("user/create/tg")
    @Headers("Content-Type: application/json")
    suspend fun newUser(@Body user: User): UserInfo
    @HTTP("GET", "user/message", hasBody = true)
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(@Body message: UserMessage): TrofikAnswer
    @GET("admin/check")
    @Headers("Content-Type: application/json")
    suspend fun checkAdmin(@QueryName tgID: Long): Boolean
    @HTTP("GET", "user/message/voice", hasBody = true)
    @Headers("Content-Type: application/octet-stream")
    suspend fun sendVoiceMessage(@Body voice: ByteArray): TrofikAnswer
}
