package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrofikAnswer(
    val message: String? = null,
    val username: String? = null,
    val question: String? = null,
    @SerialName("admin_tg_id") val adminChatID: Long? = null
)
