import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.pdmodel.graphics.color.PDColor
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB
import java.awt.Color
import java.io.File

fun getPdf(page: PDPage): PDDocument {
    val document = PDDocument()
    document.addPage(page)
    return document
}

fun getPage(): PDPage {
    val page = PDPage(
        PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width)
    )
    return page
}

fun savePdf(document: PDDocument, file: File) {
    document.save(file)
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
    // Get descender height characters such as p and q
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

fun writeTableHeader(doc: PDDocument, page: PDPage, topLeftX: Float, topLeftY: Float): BoxCoordinates {

    val cellHeight = 20f
    val cellWidth = 100f
    val bottomLeftY = topLeftY - cellHeight

    val content = "Maandag"

    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA)
    val fontSize = 12f
    val titleCapHeight = (font.fontDescriptor.capHeight / 1000) * fontSize
    // Get descender height characters such as p and q
    val titleDescent = font.fontDescriptor.descent / 1000 * fontSize
    val contentWidth = (font.getStringWidth(content) / 1000) * fontSize

    getContentStream(doc, page).use { contents ->
        contents.setStrokingColor(Color.DARK_GRAY)
        contents.setLineWidth(1f)
        contents.addRect(topLeftX, bottomLeftY, cellWidth, cellHeight);

        contents.beginText();
        contents.newLineAtOffset(topLeftX + contentWidth / 2,bottomLeftY + cellHeight / 2 - (titleCapHeight) / 2);
        contents.setFont(font, fontSize);
        contents.showText(content);
        contents.endText();

        contents.stroke();
    }

    return BoxCoordinates(topLeftX, topLeftY, topLeftX + cellWidth + 30, topLeftY - cellHeight + 10)
}

data class BoxCoordinates(val leftTopX : Float, val leftTopY: Float, val rightBottomX : Float, val rightBottomY : Float)

fun getContentStream(doc: PDDocument, page: PDPage) =
    PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)
