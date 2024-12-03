import model.BoxCoordinates
import model.Prefs
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import java.awt.Desktop
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.prefs.Preferences

fun writeAndOpenMainDocument(prefs: Preferences): String {
    val page = getRotatedA4Page()
    val doc = getDocumentOf(page)

    writeMainDocument(prefs, doc, page)

    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf").toFile()
    tempFile.deleteOnExit()

    doc.save(tempFile)
    doc.close()

    if (prefs.getBoolean(Prefs.AUTO_OPEN_PDF.key, true)) {
        // Testers can use this to remain headless
        Desktop.getDesktop().open(tempFile)
    }

    return tempFile.path
}

fun writeMainDocument(
    prefs: Preferences,
    doc: PDDocument,
    page: PDPage,
) {
    val timeProvider = TimeProvider()
    val fromLocalDateTime = getFromLDT(prefs, timeProvider)

    val fromNextMonday = pickNextMonday(fromLocalDateTime)

    val endLocalDateTime = fromNextMonday.plusWeeks(1).minusNanos(1)

    val bcHeader = writeHeader(doc, page, fromNextMonday, true)

    // Setting up table
    val fromHour = 7
    val fromMinute = 0
    val untilHour = 21
    val numberOfRows = (untilHour - fromHour) * 2
    val bcDryBox = writePage(doc, page, 0f, 0f, numberOfRows, fromHour, fromMinute, false)

    // Take the box width and center it on the page
    val tableWidth = bcDryBox.bottomRightX - bcDryBox.topLeftX
    val topLeftX = page.mediaBox.width / 2 - (tableWidth / 2)
    val bcPage = writePage(doc, page, topLeftX, bcHeader.bottomRightY - 20, numberOfRows, fromHour, fromMinute, true)

    writeSuggestions(doc, page, topLeftX, bcPage.bottomRightY, fromNextMonday, endLocalDateTime, prefs, true)
}

fun pickNextMonday(localDateTime: LocalDateTime): LocalDateTime {
    return localDateTime.with(
        TemporalAdjusters.next(DayOfWeek.MONDAY),
    ).withHour(0).withMinute(0).withSecond(0).withNano(0)
}

fun getFromLDT(
    prefs: Preferences,
    timeProvider: TimeProvider,
): LocalDateTime {
    return if (prefs.getBoolean(Prefs.START_DATE_ENABLED.key, false)) {
        try {
            val localDate = LocalDate.parse(prefs.get(Prefs.START_DATE.key, ""), formatterLD)
            LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
        } catch (e: Exception) {
            timeProvider.getLocalDateTimeNow()
        }
    } else {
        timeProvider.getLocalDateTimeNow()
    }
}

private fun writePage(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    numberOfRows: Int,
    fromHour: Int,
    fromMinute: Int,
    draw: Boolean = true,
): BoxCoordinates {
    val bc1 = writeTimeColumn(doc, page, topLeftX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc2 = writeNoteColumn(doc, page, bc1.bottomRightX, topLeftY, numberOfRows, DayOfWeek.MONDAY, draw)
    val bc3 = writeNoteColumn(doc, page, bc2.bottomRightX, topLeftY, numberOfRows, DayOfWeek.TUESDAY, draw)

    val bc4 = writeTimeColumn(doc, page, bc3.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc5 = writeNoteColumn(doc, page, bc4.bottomRightX, topLeftY, numberOfRows, DayOfWeek.WEDNESDAY, draw)
    val bc6 = writeNoteColumn(doc, page, bc5.bottomRightX, topLeftY, numberOfRows, DayOfWeek.THURSDAY, draw)

    val bc7 = writeTimeColumn(doc, page, bc6.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc8 = writeNoteColumn(doc, page, bc7.bottomRightX, topLeftY, numberOfRows, DayOfWeek.FRIDAY, draw)
    val bc9 = writeNoteColumn(doc, page, bc8.bottomRightX, topLeftY, numberOfRows, DayOfWeek.SATURDAY, draw)

    val bc10 = writeTimeColumn(doc, page, bc9.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc11 = writeNoteColumn(doc, page, bc10.bottomRightX, topLeftY, numberOfRows, DayOfWeek.SUNDAY, draw)

    return BoxCoordinates(bc1.topLeftX, bc1.topLeftY, bc11.bottomRightX, bc11.bottomRightY)
}
