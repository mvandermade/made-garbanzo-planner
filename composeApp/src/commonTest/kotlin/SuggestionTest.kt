import io.mockk.every
import io.mockk.mockk
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.Test
import java.time.LocalDateTime
import java.util.prefs.Preferences
import kotlin.test.assertEquals

class SuggestionTest {
    @Test
    fun `Daily rrule should be picked up 7 times`() {
        val mockPrefs = mockk<Preferences>()
        val page = getPage()
        val doc = getPdf(page)

        val rruleDescription = "PrintMe"

        every { mockPrefs.get("app.rrule", "") } returns "FREQ=DAILY"
        every { mockPrefs.get("app.rruleDescription", "") } returns rruleDescription

        suggestions(doc, page, 0f, 0f, LocalDateTime.now(), mockPrefs, true)

        val pdfTextStripper = PDFTextStripper()
        val text = pdfTextStripper.getText(doc)
        assertEquals("> $rruleDescription\n", text)
    }
}
