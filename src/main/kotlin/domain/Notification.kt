package domain

import kotlinx.serialization.Serializable

@Serializable
data class Notification(val users: List<User>, val message: String)
