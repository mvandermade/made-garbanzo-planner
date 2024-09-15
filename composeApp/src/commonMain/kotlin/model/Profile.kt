package model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(val id: Long, val name: String) {
    // Only compare on id
    override fun equals(other: Any?): Boolean = (other is Profile) && (this.id == other.id)

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
