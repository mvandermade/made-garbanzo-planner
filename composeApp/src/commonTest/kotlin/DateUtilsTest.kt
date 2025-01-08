import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DateUtilsTest {
    @Test
    fun `Expect week 2 to be after`() {
        assertEquals(
            2,
            getWeekNumberOfNextMonday(
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
            ),
        )
    }

    @Test
    fun `Expect week 1 to be after`() {
        assertEquals(
            1,
            getWeekNumberOfNextMonday(
                LocalDateTime.of(2024, 12, 28, 0, 0, 0),
            ),
        )
    }
}
