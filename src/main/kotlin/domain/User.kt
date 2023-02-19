package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(@SerialName("user_tg_id") val tgUserID: Long, val username: String? = null, @SerialName("first_name") val firstName: String? = null)
