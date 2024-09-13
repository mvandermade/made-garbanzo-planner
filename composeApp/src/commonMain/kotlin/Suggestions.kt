import net.fortuna.ical4j.model.Recur
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.time.LocalDateTime
import java.util.prefs.Preferences

fun suggestions(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    fromLocalDateTime: LocalDateTime,
    prefs: Preferences,
    draw: Boolean = true,
): BoxCoordinates {
    var text = ""
    try {
        val recur = Recur<LocalDateTime>(prefs.get("app.rrule", ""))
        // Remove the smallest imaginable entity to make it non-inclusive
        val end = fromLocalDateTime.plusWeeks(1).minusNanos(1)

        if (recur.getDates(fromLocalDateTime, end).size > 0) {
            text = "> ${prefs.get("app.rruleDescription", "")}"
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val font = PDType1Font(Standard14Fonts.FontName.COURIER)
    val fontSize = 12f
    val verticalSpacing = 25f
    return writeText(doc, page, topLeftX, topLeftY - verticalSpacing, text, font, fontSize, draw)
}
