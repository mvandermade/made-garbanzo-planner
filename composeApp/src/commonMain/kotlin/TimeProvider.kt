import net.fortuna.ical4j.model.Recur
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeProvider {
    fun getLocalDateTimeNow(): LocalDateTime = LocalDateTime.now()
}

val formatLDT = "dd-MM-yyyy HH:mm"
val formatterLDT = DateTimeFormatter.ofPattern(formatLDT)

val formatLD = "dd-MM-yyyy"
val formatterLD = DateTimeFormatter.ofPattern(formatLD)

fun getRRuleDates(
    rrule: String,
    seed: LocalDateTime,
    fromLocalDateTime: LocalDateTime,
    endLocalDateTime: LocalDateTime,
): List<LocalDateTime> {
    val recur = Recur<LocalDateTime>(rrule)
    return recur.getDates(seed, fromLocalDateTime, endLocalDateTime)
}
