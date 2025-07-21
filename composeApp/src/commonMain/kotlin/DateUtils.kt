import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

fun getWeekNumberOfNextMonday(fromLocalDateTime: LocalDateTime): Int {
    val fromNextMonday = pickNextMonday(fromLocalDateTime)
    return fromNextMonday.get(WeekFields.ISO.weekOfWeekBasedYear())
}

fun pickNextMonday(localDateTime: LocalDateTime): LocalDateTime =
    localDateTime
        .with(
            TemporalAdjusters.next(DayOfWeek.MONDAY),
        ).withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
