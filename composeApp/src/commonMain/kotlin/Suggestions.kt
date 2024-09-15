import kotlinx.serialization.json.Json
import model.Prefs
import model.RRuleSet
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
    val rruleSetsSet =
        try {
            val list = Json.decodeFromString<List<RRuleSet>>(prefs.get(Prefs.RRULE_SETS.key, "[]"))
            list
                .filter { it.profileId == prefs.get(Prefs.ACTIVE_PROFILE.key, "0").toLong() }
                .sortedBy { it.id }
                .toMutableSet()
        } catch (e: Exception) {
            mutableSetOf()
        }

    var text = ""
    // Remove the smallest imaginable entity to make it non-inclusive
    val end = fromLocalDateTime.plusWeeks(1).minusNanos(1)
    rruleSetsSet.forEach {
        try {
            val recur = Recur<LocalDateTime>(it.rrule)
            if (recur.getDates(fromLocalDateTime, end).size > 0) {
                text += "> ${it.description} <"
            }
        } catch (e: Exception) {
            println("Could not parse ${it.description}")
        }
    }

    val font = PDType1Font(Standard14Fonts.FontName.COURIER)
    val fontSize = 12f
    val verticalSpacing = 25f
    return writeText(doc, page, topLeftX, topLeftY - verticalSpacing, text, font, fontSize, draw)
}
