import kotlinx.serialization.json.Json
import model.Prefs
import model.RRuleSet
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
    endLocalDateTime: LocalDateTime,
    prefs: Preferences,
    draw: Boolean = true,
): BoxCoordinates {
    val list = Json.decodeFromString<List<RRuleSet>>(prefs.get(Prefs.RRULE_SETS.key, "[]"))
    val rruleSetsSet =
        list
            .filter { it.profileId == prefs.get(Prefs.ACTIVE_PROFILE.key, "0").toLong() }
            .sortedBy { it.id }
            .toMutableSet()

    var text = "${rruleSetsSet.size}: "
    // Remove the smallest imaginable entity to make it non-inclusive

    var counter = 0
    rruleSetsSet.forEach { rruleSet ->
        try {
            val seed = LocalDateTime.parse(rruleSet.fromLDT, formatterLDT)
            val dates = getRRuleDates(rruleSet.rrule, seed, fromLocalDateTime, endLocalDateTime)
            if (dates.isNotEmpty()) {
                text += "> ${rruleSet.description} <"
                counter++
            }
        } catch (e: Exception) {
            text += "> !error ${rruleSet.description} <"
            println("Could not parse ${rruleSet.description}")
            e.printStackTrace()
        }
    }

    if (counter == 0) {
        text += "0 regels..."
    }

    val font = PDType1Font(Standard14Fonts.FontName.COURIER)
    val fontSize = 12f
    val verticalSpacing = 25f
    return writeText(doc, page, topLeftX, topLeftY - verticalSpacing, text, font, fontSize, draw)
}
