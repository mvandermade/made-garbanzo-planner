import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.pdmodel.graphics.color.PDColor
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Color
import java.awt.Desktop


@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { writeAndOpenPdfToTemp() }) {
                Text("Generate PDF")
            }
        }
    }
}

fun writeAndOpenPdfToTemp() {
    val page = getPage()
    val doc = getPdf(page)

    val headerMessage = "Keekplanner"
    val headerCoordinates = writeHeader(doc, page, headerMessage)
    drawBox(doc, page, headerCoordinates)
    writeTableHeader(doc, page, 20f, headerCoordinates.rightBottomY - 10)

    // Saving
    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf")
    tempFile.toFile().deleteOnExit()
    savePdf(doc, tempFile.toFile())
    doc.close()
    Desktop.getDesktop().open(tempFile.toFile())
}
