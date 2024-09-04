import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Desktop
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters


@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { writeAndOpenPdfToTemp() }, modifier = Modifier.fillMaxHeight()) {
                Text("Generate PDF")
            }
        }
    }
}

fun writeAndOpenPdfToTemp() {
    val page = getPage()
    val doc = getPdf(page)

    val headerMessage = "Weekplanner"
    val marginTop = 20f
    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
    val fontSize = 14f
    val xOffset = (page.getMediaBox().width) / 2
    val yOffset = page.getMediaBox().height - marginTop

    val headerCoordinates = writeText(doc, page, xOffset, yOffset, headerMessage, font, fontSize)
    val fromHour = 7
    val fromMinute = 0
    val untilHour = 21
    val numberOfRows = (untilHour - fromHour) * 2
    val topLeftY = headerCoordinates.bottomRightY - 20

    val bc1 = writeFirstColumn(doc, page, 10f, topLeftY, numberOfRows, fromHour, fromMinute)

    val bc2 = writeSecondColumn(doc, page, bc1.bottomRightX, topLeftY, DayOfWeek.MONDAY, numberOfRows)
    val bc3 = writeSecondColumn(doc, page, bc2.bottomRightX, topLeftY, DayOfWeek.TUESDAY, numberOfRows)

    val bc4 = writeFirstColumn(doc, page, bc3.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute)

    val bc5 = writeSecondColumn(doc, page, bc4.bottomRightX, topLeftY, DayOfWeek.WEDNESDAY, numberOfRows)
    val bc6 = writeSecondColumn(doc, page, bc5.bottomRightX, topLeftY, DayOfWeek.THURSDAY, numberOfRows)

    val bc7 = writeFirstColumn(doc, page, bc6.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute)

    val bc8 = writeSecondColumn(doc, page, bc7.bottomRightX, topLeftY, DayOfWeek.FRIDAY, numberOfRows)
    val bc9 = writeSecondColumn(doc, page, bc8.bottomRightX, topLeftY, DayOfWeek.SATURDAY, numberOfRows)

    val bc10 = writeFirstColumn(doc, page, bc9.bottomRightX, topLeftY, numberOfRows, fromHour, fromMinute)

    val bc11 = writeSecondColumn(doc, page, bc10.bottomRightX, topLeftY, DayOfWeek.SUNDAY, numberOfRows)




    // Saving
    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf")
    tempFile.toFile().deleteOnExit()
    savePdf(doc, tempFile.toFile())
    doc.close()
    Desktop.getDesktop().open(tempFile.toFile())
}
