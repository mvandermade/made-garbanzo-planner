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

    val headerMessage = "Weekplanner"
    val headerCoordinates = writeHeader(doc, page, headerMessage)
    drawBox(doc, page, headerCoordinates)
    writeTableHeader(doc, page, 200f)

    // Saving
    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf")
    tempFile.toFile().deleteOnExit()
    savePdf(doc, tempFile.toFile())
    doc.close()
    Desktop.getDesktop().open(tempFile.toFile())
}

fun drawBox(doc: PDDocument, page: PDPage, coordinates: BoxCoordinates) {
    // The coordinates start at the top left. But drawing a box starts at the bottom left.
    val width = coordinates.rightBottomX - coordinates.leftTopX
    val height = coordinates.leftTopY - coordinates.rightBottomY

    val RED = PDColor(floatArrayOf(1f, 0f, 0f), PDDeviceRGB.INSTANCE)

    getContentStream(doc, page).use { contents ->
        contents.setStrokingColor(RED)
        contents.setLineWidth(1f)
        contents.addRect(
            coordinates.leftTopX, coordinates.rightBottomY, width, height
        )
    }
}

fun writeHeader(doc: PDDocument, page: PDPage, title: String): BoxCoordinates {
    val marginTop = 30f
    val font = PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN)
    val fontSize = 12f
    val titleWidth = (font.getStringWidth(title) / 1000) * fontSize
    val titleCapHeight = (font.fontDescriptor.capHeight / 1000) * fontSize
    // Get descender height
    val titleDescent = font.fontDescriptor.descent / 1000 * fontSize

    val xOffset = (page.getMediaBox().width - titleWidth) / 2
    // Take the descenders into account later
    val yOffset = page.getMediaBox().height - marginTop - titleCapHeight

    getContentStream(doc, page).use { contents ->
        contents.beginText()
        contents.setFont(font, fontSize)
        contents.newLineAtOffset(xOffset, yOffset)
        contents.showText(title)
        contents.endText()
    }
    // Note because of the yOffset the rectangle starts with the offset
    return BoxCoordinates(xOffset,
        yOffset + titleCapHeight, // The font is as high as the cap height
        xOffset + titleWidth, // The amount of characters times box width
        yOffset + titleDescent // Take into account the descenders such as p and q (negative value)
    )

}

fun writeTableHeader(doc: PDDocument, page: PDPage, initX: Float): BoxCoordinates {
    val initY = page.getMediaBox().height - 50

    val cellHeight = 20f
    val cellWidth = 100f

    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA)
    val fontSize = 12f

    getContentStream(doc, page).use { contents ->
        contents.setStrokingColor(Color.DARK_GRAY)
        contents.setLineWidth(1f)
        contents.addRect(initX, initY, cellWidth + 30, -cellHeight);

        contents.beginText();
        contents.newLineAtOffset(initX + 30, initY - cellHeight + 10);
        contents.setFont(font, fontSize);
        contents.showText("Tok");
        contents.endText();

        contents.stroke();
    }

    return BoxCoordinates(initX, initY, initX + cellWidth + 30, initY - cellHeight + 10)
}

data class BoxCoordinates(val leftTopX : Float, val leftTopY: Float, val rightBottomX : Float, val rightBottomY : Float)

fun getContentStream(doc: PDDocument, page: PDPage) =
    PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)
