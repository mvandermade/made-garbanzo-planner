import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.dmfs.rfc5545.recurrenceset.OfRule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeProvider {
    fun getLocalDateTimeNow(): LocalDateTime = LocalDateTime.now()
}

val formatLDT = "dd-MM-yyyy HH:mm"
val formatterLDT = DateTimeFormatter.ofPattern(formatLDT)

val formatLD = "dd-MM-yyyy"
val formatterLD = DateTimeFormatter.ofPattern(formatLD)

val formatRecurLDT = "yyyyMMdd'T'HHmmss"
val formatterRecurLDT = DateTimeFormatter.ofPattern(formatRecurLDT)

fun isRRuleInDateTimeFrame(
    rruleString: String,
    seed: LocalDateTime,
    fromLocalDateTime: LocalDateTime,
    endLocalDateTime: LocalDateTime,
): Boolean {
    val rrule = RecurrenceRule(rruleString)

    val ofRule = OfRule(rrule, DateTime.parse(seed.format(formatterRecurLDT)))

    val fromDateTime = DateTime.parse(fromLocalDateTime.format(formatterRecurLDT))
    val endDateTime = DateTime.parse(endLocalDateTime.format(formatterRecurLDT))

    // Prevent infinite loops
    val ofRuleIterator = ofRule.iterator()
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
    throw IllegalStateException("Too many loops, compact the RRule start date")
}
