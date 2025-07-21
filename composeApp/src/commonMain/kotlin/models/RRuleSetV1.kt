package models

import kotlinx.serialization.Serializable

@Serializable
data class RRuleSetV1(
    val profileId: Long,
    val id: Long,
    val description: String,
    val rrule: String,
    val fromLDT: String,
) {
    // Only compare on id
    override fun equals(other: Any?): Boolean = (other is RRuleSetV1) && (this.id == other.id)

    override fun hashCode(): Int = id.hashCode()
}
