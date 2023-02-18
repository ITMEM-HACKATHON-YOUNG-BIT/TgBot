package domain

import kotlinx.serialization.Serializable

@Serializable
data class User(val tgUserID: Long, val username: String? = null, val firstName: String? = null)
