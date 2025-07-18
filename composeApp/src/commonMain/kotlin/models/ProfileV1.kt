package models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileV1(
    val id: Long,
    val name: String,
) {
    // Only compare on id
    override fun equals(other: Any?): Boolean = (other is ProfileV1) && (this.id == other.id)

    override fun hashCode(): Int = id.hashCode()
}
