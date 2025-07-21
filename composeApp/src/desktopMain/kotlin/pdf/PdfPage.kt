package pdf

import TimeProvider
import formatterLD
import models.BoxCoordinates
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import pickNextMonday
import repositories.PreferencesStore
import java.awt.Desktop
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.io.path.createTempFile

fun writeAndOpenMainDocument(preferencesStore: PreferencesStore): String {
    val page = getRotatedA4Page()
    val doc = getDocumentOf(page)

    writeMainDocument(preferencesStore, doc, page)

    val tempFile = createTempFile("planner-101-", suffix = ".pdf").toFile()
    tempFile.deleteOnExit()

    doc.save(tempFile)
    doc.close()

    if (preferencesStore.autoOpenPDFAfterGenerationIsEnabled) {
        // Testers can use this to remain headless
        Desktop.getDesktop().open(tempFile)
    }

    return tempFile.path
}

fun writeMainDocument(
    preferencesStore: PreferencesStore,
    doc: PDDocument,
    page: PDPage,
) {
    val timeProvider = TimeProvider()
    val fromLocalDateTime = getFromLDT(preferencesStore, timeProvider)

    val fromNextMonday = pickNextMonday(fromLocalDateTime)

    val endLocalDateTime = fromNextMonday.plusWeeks(1).minusNanos(1)

    val bcHeader = writeHeader(doc, page, fromLocalDateTime, true)

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

    writeSuggestions(doc, page, topLeftX, bcPage.bottomRightY, fromNextMonday, endLocalDateTime, preferencesStore, true)
}

fun getFromLDT(
    preferencesStore: PreferencesStore,
    timeProvider: TimeProvider,
): LocalDateTime =
    if (preferencesStore.startDateIsEnabled) {
        try {
            val localDate = LocalDate.parse(preferencesStore.startDate, formatterLD)
            LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
        } catch (e: Exception) {
            timeProvider.getLocalDateTimeNow()
        }
    } else {
        timeProvider.getLocalDateTimeNow()
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
