package domain

import kotlinx.serialization.Serializable

@Serializable
data class UserMessage(val tgChatID: Long, val message: String)
