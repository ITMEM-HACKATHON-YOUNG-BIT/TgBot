package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMessage(@SerialName("user_tg_id") val tgChatID: Long, val message: String)
