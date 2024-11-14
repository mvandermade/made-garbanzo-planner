import kotlinx.serialization.json.Json
import model.BoxCoordinates
import model.Prefs
import model.RRuleSet
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.prefs.Preferences
import kotlin.math.floor

fun writeSuggestions(
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
            if (isRRuleInDateTimeFrame(rruleSet.rrule, seed, fromLocalDateTime, endLocalDateTime)) {
                text += "> ${rruleSet.description} <"
                counter++
            }
        } catch (e: Exception) {
            text += "> !error ${rruleSet.description} ${e.message} <"
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

fun writeHeader(
    doc: PDDocument,
    page: PDPage,
    fromDateTime: LocalDateTime,
    draw: Boolean = true,
): BoxCoordinates {
    val weekNumber = fromDateTime.get(ChronoField.ALIGNED_WEEK_OF_YEAR)

    val headerMessage = "Weekplanner $weekNumber"
    val marginTop = 40f
    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
    val fontSize = 14f
    val headerWidth = (font.getStringWidth(headerMessage) / 1000) * fontSize
    val xOffset = page.mediaBox.width / 2 - (headerWidth / 2)
    val yOffset = page.mediaBox.height - marginTop
    val headerCoordinates = writeText(doc, page, xOffset, yOffset, headerMessage, font, fontSize, draw)
    return headerCoordinates
}

fun writeTimeColumn(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    numberOfRows: Int,
    fromHour: Int,
    fromMinute: Int,
    draw: Boolean = true,
): BoxCoordinates {
    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE)
    val fontSize = 8f

    val timeHeaderWidth = 27f
    val timeHeaderHeight = 35f
    val bc = writeCell(doc, page, topLeftX, topLeftY, timeHeaderWidth, timeHeaderHeight, draw)

    var yOffset = bc.bottomRightY

    // Text spawns at the bottom so up it at the beginning of the loop
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    var loopTime = LocalDateTime.of(2000, 1, 1, fromHour, fromMinute, 0)

    val verticalSpacing = 15f
    for (i in 0..numberOfRows) {
        yOffset -= verticalSpacing
        val text = loopTime.format(formatter)
        val xOffset = bc.topLeftX + timeHeaderWidth / 2 - (font.getStringWidth(text) / 1000 * fontSize) / 2

        writeText(doc, page, xOffset, yOffset, text, font, fontSize, draw)
        loopTime = loopTime.plusMinutes(30)
    }

    // Lower boundary
    val lowerPadding = 4f
    yOffset -= lowerPadding

    if (draw) {
        writeBox(doc, page, BoxCoordinates(bc.topLeftX, bc.bottomRightY, bc.bottomRightX, yOffset))
    }
    return BoxCoordinates(topLeftX, topLeftY, bc.bottomRightX, yOffset)
}

fun writeNoteColumn(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    dayOfWeek: DayOfWeek,
    numberOfRows: Int,
    draw: Boolean = true,
): BoxCoordinates {
    val day =
        when (dayOfWeek) {
            DayOfWeek.MONDAY -> "maandag"
            DayOfWeek.TUESDAY -> "dinsdag"
            DayOfWeek.WEDNESDAY -> "woensdag"
            DayOfWeek.THURSDAY -> "donderdag"
            DayOfWeek.FRIDAY -> "vrijdag"
            DayOfWeek.SATURDAY -> "zaterdag"
            DayOfWeek.SUNDAY -> "zondag"
        }

    val bc = writeTextAndCell(doc, page, topLeftX, topLeftY, day, cellHeight = 35f, cellWidth = 95f, draw = draw)

    val verticalSpacing = 15f
    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA)
    val fontSize = 8f

    val startMargin = floor(font.fontDescriptor.descent / 1000 * fontSize)

    val xOffset = bc.topLeftX + font.getStringWidth(".") / 1000 * fontSize
    var yOffset = bc.bottomRightY + startMargin

    // Try to fill the whole line
    val maxWidth = bc.bottomRightX - xOffset
    val oneCharWidth = font.getStringWidth(".") / 1000 * fontSize
    val template = ".".repeat((maxWidth / oneCharWidth).toInt())

    for (i in 0..numberOfRows) {
        yOffset -= verticalSpacing
        writeText(doc, page, xOffset, yOffset, template, font, fontSize, draw)
    }

    val lowerPadding = 4f

    yOffset -= (lowerPadding + startMargin)

    if (draw) {
        writeBox(doc, page, BoxCoordinates(bc.topLeftX, bc.bottomRightY, bc.bottomRightX, yOffset))
    }

    return BoxCoordinates(topLeftX, topLeftY, bc.bottomRightX, yOffset)
}