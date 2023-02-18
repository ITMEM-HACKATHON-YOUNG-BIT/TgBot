package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @SerialName("first_name") val firstName: String,
    @SerialName("second_name") val secondName: String
) {
    override fun toString() = "$firstName $secondName"
}
