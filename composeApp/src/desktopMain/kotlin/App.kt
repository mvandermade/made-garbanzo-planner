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
    val marginTop = 30f
    val font = PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN)
    val fontSize = 12f
    val xOffset = (page.getMediaBox().width) / 2
    val yOffset = page.getMediaBox().height - marginTop

    val headerCoordinates = writeText(doc, page, xOffset, yOffset, headerMessage, font, fontSize)
    drawBox(doc, page, headerCoordinates)
    writeFirstColumn(doc, page, 70f, headerCoordinates.bottomRightY - 40)
    writeTextAndCell(doc, page, 10f, 10f, "maandag")

    // Saving
    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf")
    tempFile.toFile().deleteOnExit()
    savePdf(doc, tempFile.toFile())
    doc.close()
    Desktop.getDesktop().open(tempFile.toFile())
}
