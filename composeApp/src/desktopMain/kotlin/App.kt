import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Desktop
import java.time.DayOfWeek
import java.time.temporal.ChronoField


@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { writeAndOpenPdfToTemp() }, modifier = Modifier.fillMaxHeight()) {
                Text("Generate PDF (or press ENTER)")
            }
        }
    }
}

fun writeAndOpenPdfToTemp() {
    val timeProvider = TimeProvider()
    val page = getPage()
    val doc = getPdf(page)

    val bcHeader = header(doc, page, timeProvider, true)

    // Setting up table
    val fromHour = 7
    val fromMinute = 0
    val untilHour = 21
    val numberOfRows = (untilHour - fromHour) * 2
    val bcDryBox = page(doc, page, 0f, 0f, numberOfRows, fromHour, fromMinute, false)

    // Take the box width and center it on the page
    val tableWidth = bcDryBox.bottomRightX - bcDryBox.topLeftX
    val topLeftX = page.mediaBox.width / 2 - (tableWidth / 2)
    val bcPage = page(doc, page, topLeftX, bcHeader.bottomRightY - 20, numberOfRows, fromHour, fromMinute, true)

    suggestions(doc, page, topLeftX, bcPage.bottomRightY, true)

    // Saving and opening
    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf").toFile()
    tempFile.deleteOnExit()
    savePdf(doc, tempFile)
    doc.close()
    Desktop.getDesktop().open(tempFile)
}

private fun header(
    doc: PDDocument,
    page: PDPage,
    timeProvider: TimeProvider,
    draw: Boolean = true
): BoxCoordinates {
    val date = timeProvider.getLocalDateNow()
    val weekNumber = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

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

fun page(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    numberOfRows: Int,
    fromHour: Int,
    fromMinute: Int,
    draw: Boolean = true,
): BoxCoordinates {
    val bc1 = doFirstColumn(doc, page, topLeftX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc2 = doSecondColumn(doc, page, bc1.bottomRightX, topLeftY, DayOfWeek.MONDAY, numberOfRows, draw)
    val bc3 = doSecondColumn(doc, page, bc2.bottomRightX, topLeftY, DayOfWeek.TUESDAY, numberOfRows, draw)

    val bc4 = doFirstColumn(doc, page, bc3.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc5 = doSecondColumn(doc, page, bc4.bottomRightX, topLeftY, DayOfWeek.WEDNESDAY, numberOfRows, draw)
    val bc6 = doSecondColumn(doc, page, bc5.bottomRightX, topLeftY, DayOfWeek.THURSDAY, numberOfRows, draw)

    val bc7 = doFirstColumn(doc, page, bc6.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc8 = doSecondColumn(doc, page, bc7.bottomRightX, topLeftY, DayOfWeek.FRIDAY, numberOfRows, draw)
    val bc9 = doSecondColumn(doc, page, bc8.bottomRightX, topLeftY, DayOfWeek.SATURDAY, numberOfRows, draw)

    val bc10 = doFirstColumn(doc, page, bc9.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute, draw)

    val bc11 = doSecondColumn(doc, page, bc10.bottomRightX, topLeftY, DayOfWeek.SUNDAY, numberOfRows, draw)

    return BoxCoordinates(bc1.topLeftX, bc1.topLeftY, bc11.bottomRightX, bc11.bottomRightY)
}

fun suggestions(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    draw: Boolean = true,
): BoxCoordinates {
    val text = "Hello this is some suggestion!"
    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE)
    val fontSize = 8f
    val verticalSpacing = 25f
    return writeText(doc, page, topLeftX, topLeftY - verticalSpacing, text, font, fontSize, draw)

}
