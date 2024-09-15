package model

import kotlinx.serialization.Serializable

@Serializable
data class RRuleSet(val profileId: Long, val id: Long, val description: String, val rrule: String) {
    // Only compare on id
    override fun equals(other: Any?): Boolean = (other is RRuleSet) && (this.id == other.id)

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
