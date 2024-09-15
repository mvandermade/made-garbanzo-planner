import io.mockk.every
import io.mockk.mockk
import model.Prefs
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.Test
import java.time.LocalDateTime
import java.util.prefs.Preferences
import kotlin.test.assertEquals

class SuggestionTest {
    @Test
    fun `Daily rrule should be picked up`() {
        val mockPrefs = mockk<Preferences>()
        val page = getPage()
        val doc = getPdf(page)

        val rruleDescription = "PrintMe"

        every { mockPrefs.get(Prefs.ACTIVE_PROFILE.key, "0") } returns "1"

        every { mockPrefs.get(Prefs.RRULE_SETS.key, "[]") } returns
            """
            [
            { "profileId": 1, "id": 2, "description": "$rruleDescription", "rrule": "FREQ=DAILY" }
            ]
            """.trimIndent()

        suggestions(doc, page, 0f, 0f, LocalDateTime.now(), mockPrefs, true)

        val pdfTextStripper = PDFTextStripper()
        val text = pdfTextStripper.getText(doc)
        assertEquals("> $rruleDescription <\n", text)
    }

    @Test
    fun `Three daily rrule should be picked up`() {
        val mockPrefs = mockk<Preferences>()
        val page = getPage()
        val doc = getPdf(page)

        every { mockPrefs.get(Prefs.ACTIVE_PROFILE.key, "0") } returns "1"

        every { mockPrefs.get(Prefs.RRULE_SETS.key, "[]") } returns
            """
            [
            { "profileId": 1, "id": 2, "description": "PrintMe", "rrule": "FREQ=DAILY" },
            { "profileId": 1, "id": 44, "description": "PrintMe2", "rrule": "FREQ=DAILY" },
            { "profileId": 1, "id": 60, "description": "PrintMe3", "rrule": "FREQ=DAILY" }
            ]
            """.trimIndent()

        suggestions(doc, page, 0f, 0f, LocalDateTime.now(), mockPrefs, true)

        val pdfTextStripper = PDFTextStripper()
        val text = pdfTextStripper.getText(doc)
        assertEquals("> PrintMe <> PrintMe2 <> PrintMe3 <\n", text)
    }

    @Test
    fun `Ignore faulty RRULE`() {
        val mockPrefs = mockk<Preferences>()
        val page = getPage()
        val doc = getPdf(page)

        every { mockPrefs.get(Prefs.ACTIVE_PROFILE.key, "0") } returns "1"

        every { mockPrefs.get(Prefs.RRULE_SETS.key, "[]") } returns
            """
            [
            { "profileId": 1, "id": 2, "description": "PrintMe", "rrule": "FREQ=DAILY" },
            { "profileId": 1, "id": 3, "description": "NO_PRINT", "rrule": "BIG_OOF" }
            ]
            """.trimIndent()

        suggestions(doc, page, 0f, 0f, LocalDateTime.now(), mockPrefs, true)

        val pdfTextStripper = PDFTextStripper()
        val text = pdfTextStripper.getText(doc)
        assertEquals("> PrintMe <\n", text)
    }

    @Test
    fun `Ignore RRULE with non unique id`() {
        val mockPrefs = mockk<Preferences>()
        val page = getPage()
        val doc = getPdf(page)

        every { mockPrefs.get(Prefs.ACTIVE_PROFILE.key, "0") } returns "1"

        every { mockPrefs.get(Prefs.RRULE_SETS.key, "[]") } returns
            """
            [
            { "profileId": 1, "id": 2, "description": "PrintMe", "rrule": "FREQ=DAILY" },
            { "profileId": 1, "id": 2, "description": "NO_PRINT", "rrule": "FREQ=DAILY" }
            ]
            """.trimIndent()

        suggestions(doc, page, 0f, 0f, LocalDateTime.now(), mockPrefs, true)

        val pdfTextStripper = PDFTextStripper()
        val text = pdfTextStripper.getText(doc)
        assertEquals("> PrintMe <\n", text)
    }

    @Test
    fun `Ignore RRULE of other profile`() {
        val mockPrefs = mockk<Preferences>()
        val page = getPage()
        val doc = getPdf(page)

        every { mockPrefs.get(Prefs.ACTIVE_PROFILE.key, "0") } returns "1"

        every { mockPrefs.get(Prefs.RRULE_SETS.key, "[]") } returns
            """
            [
            { "profileId": 1, "id": 2, "description": "PrintMe", "rrule": "FREQ=DAILY" },
            { "profileId": 2, "id": 3, "description": "NO_PRINT", "rrule": "FREQ=DAILY" }
            ]
            """.trimIndent()

        suggestions(doc, page, 0f, 0f, LocalDateTime.now(), mockPrefs, true)

        val pdfTextStripper = PDFTextStripper()
        val text = pdfTextStripper.getText(doc)
        assertEquals("> PrintMe <\n", text)
    }
}
