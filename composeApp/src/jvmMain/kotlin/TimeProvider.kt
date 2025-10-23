import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.dmfs.rfc5545.recurrenceset.OfRule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeProvider {
    fun getLocalDateTimeNow(): LocalDateTime = LocalDateTime.now()
}

const val FORMAT_LDT = "dd-MM-yyyy HH:mm"
val formatterLDT: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LDT)

const val FORMAT_LD = "dd-MM-yyyy"
val formatterLD: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LD)

const val FORMAT_RECUR_LDT = "yyyyMMdd'T'HHmmss"
val formatterRecurLDT: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_RECUR_LDT)

fun isRRuleInDateTimeFrame(
    rruleString: String,
    seed: LocalDateTime,
    fromLocalDateTime: LocalDateTime,
    endLocalDateTime: LocalDateTime,
): Boolean {
    val rrule = RecurrenceRule(rruleString)

    val ofRuleSeed = OfRule(rrule, DateTime.parse(seed.format(formatterRecurLDT)))

    val fromDateTime = DateTime.parse(fromLocalDateTime.format(formatterRecurLDT))
    val endDateTime = DateTime.parse(endLocalDateTime.format(formatterRecurLDT))

    // Prevent infinite loops
    val ofRuleIterator = ofRuleSeed.iterator()
    for (i in 0..1000) {
        if (!ofRuleIterator.hasNext()) {
            // Nothing found here...
            return false
        }
        val nextDateTime = ofRuleIterator.next()
        if (nextDateTime.after(fromDateTime) && nextDateTime.before(endDateTime)) {
            return true
        } else if (nextDateTime.after(endDateTime)) {
            return false
        }
    }
    throw IllegalStateException("Too many loops, compact the recurrence rule start date")
}

sealed class LocalDateTimeResult {
    data class Success(
        val localDateTime: LocalDateTime,
    ) : LocalDateTimeResult()

    data object NotFound : LocalDateTimeResult()
}

fun getFirstOfRRule(
    rruleString: String,
    seed: LocalDateTime,
    fromLocalDateTime: LocalDateTime,
): LocalDateTimeResult {
    val rrule = RecurrenceRule(rruleString)

    val ofRuleSeed = OfRule(rrule, DateTime.parse(seed.format(formatterRecurLDT)))
    val fromDateTime = DateTime.parse(fromLocalDateTime.format(formatterRecurLDT))

    val ofRuleIterator = ofRuleSeed.iterator()
    for (i in 0..1000) {
        if (!ofRuleIterator.hasNext()) {
            return LocalDateTimeResult.NotFound
        }
        val nextDateTime = ofRuleIterator.next()
        if (nextDateTime.after(fromDateTime)) {
            return LocalDateTimeResult.Success(recurDateTimeToLDT(nextDateTime))
        }
    }

    return LocalDateTimeResult.NotFound
}

// If you use the month attribute, it starts at 0 it seems... so first convert it to string
fun recurDateTimeToLDT(dateTime: DateTime): LocalDateTime = LocalDateTime.parse(dateTime.toString(), formatterRecurLDT)
