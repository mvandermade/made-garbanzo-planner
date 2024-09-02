import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

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

fun writeText(
    doc: PDDocument, page: PDPage, xOffset: Float, yOffset: Float, message: String, font: PDFont, fontSize: Float
): BoxCoordinates {

    val titleWidth = (font.getStringWidth(message) / 1000) * fontSize
    val titleCapHeight = (font.fontDescriptor.capHeight / 1000) * fontSize
    // Get descender height characters such as p and q
    val titleDescent = font.fontDescriptor.descent / 1000 * fontSize

    getContentStream(doc, page).use { contents ->
        contents.beginText()
        contents.setFont(font, fontSize)
        contents.newLineAtOffset(xOffset, yOffset)
        contents.showText(message)
        contents.endText()
        contents.stroke()
    }
    // Note because of the yOffset the rectangle starts with the offset
    return BoxCoordinates(xOffset,
        yOffset + titleCapHeight, // The font is as high as the cap height
        xOffset + titleWidth, // The amount of characters times box width
        yOffset + titleDescent // Take into account the descenders such as p and q (negative value)
    )

}

fun writeFirstColumn(doc: PDDocument, page: PDPage, topLeftX: Float, topLeftY: Float): BoxCoordinates {
    val fromTime = 7
    val fromMinute = 0
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val untilHour = 21
    val untilMinute = 0

    val font = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE)
    val fontSize = 8f

    val timeHeaderWidth = 30f
    val timeHeaderHeight = 35f
    val bc = writeCell(doc, page, topLeftX, topLeftY, timeHeaderWidth, timeHeaderHeight)
    val initialTime = LocalDateTime.now()
    var rollingTime = initialTime
        .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        .withHour(fromTime).withMinute(fromMinute)

    val verticalSpacing = 15f

    val xOffset = bc.topLeftX + font.getStringWidth("07") / 1000
    var yOffset = bc.bottomRightY

    // Text spawns at the bottom so up it at the beginning of the loop

    for (i in 0..1000) {
        yOffset -= verticalSpacing
        writeText(doc, page, xOffset, yOffset, rollingTime.format(formatter), font, fontSize)
        if (rollingTime.hour == untilHour && rollingTime.minute == untilMinute) {
            break
        }
        rollingTime = rollingTime.plusMinutes(30)
    }
    yOffset -= verticalSpacing

    drawBox(doc, page, BoxCoordinates(bc.topLeftX, bc.bottomRightY, bc.bottomRightX, yOffset))

    return BoxCoordinates(topLeftX, topLeftY, topLeftX, topLeftY)
}

fun drawBox(doc: PDDocument, page: PDPage, coordinates: BoxCoordinates) {
    // The coordinates start at the top left. But drawing a box starts at the bottom left.
    val width = coordinates.bottomRightX - coordinates.topLeftX
    val height = coordinates.topLeftY - coordinates.bottomRightY

    getContentStream(doc, page).use { contents ->
        contents.setLineWidth(1f)
        contents.addRect(
            coordinates.topLeftX, coordinates.bottomRightY, width, height
        )
    }
}

fun writeCell(
    doc: PDDocument, page: PDPage,
    topLeftX: Float, topLeftY: Float,
    cellWidth: Float, cellHeight: Float,
): BoxCoordinates {
    val bottomLeftY = topLeftY - cellHeight

    getContentStream(doc, page).use { contents ->
        contents.setLineWidth(1f)
        contents.addRect(topLeftX, bottomLeftY, cellWidth, cellHeight)
        contents.stroke()
    }

    return BoxCoordinates(topLeftX, topLeftY, topLeftX + cellWidth, bottomLeftY)
}

fun writeTextAndCell(
    doc: PDDocument, page: PDPage,
    topLeftX: Float, topLeftY: Float,
    content: String,
    cellWidth: Float = 100f, cellHeight: Float = 20f,
    font: PDFont = PDType1Font(Standard14Fonts.FontName.HELVETICA), fontSize: Float = 12f,
): BoxCoordinates {
    val bottomLeftY = topLeftY - cellHeight
    val titleCapHeight = (font.fontDescriptor.xHeight / 1000) * fontSize
    val contentWidth = (font.getStringWidth(content) / 1000) * fontSize

    writeCell(doc, page, topLeftX, topLeftY, cellWidth, cellHeight)
    getContentStream(doc, page).use { contents ->
        contents.beginText();
        contents.newLineAtOffset(topLeftX + contentWidth / 2,bottomLeftY + cellHeight / 2 - (titleCapHeight) / 2);
        contents.setFont(font, fontSize);
        contents.showText(content)
        contents.endText()
        contents.stroke()
    }

    return BoxCoordinates(topLeftX, topLeftY, topLeftX + cellWidth + 30, topLeftY - cellHeight + 10)
}


data class BoxCoordinates(val topLeftX : Float, val topLeftY: Float, val bottomRightX : Float, val bottomRightY : Float)
data class BoxCoordinatesHolder(var topLeftX : Float, var topLeftY: Float, var bottomRightX : Float, var bottomRightY : Float)

fun BoxCoordinatesHolder.toBoxCoordinates(): BoxCoordinates =
    BoxCoordinates(topLeftX, topLeftY, bottomRightX, bottomRightY)

fun getContentStream(doc: PDDocument, page: PDPage) =
    PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)
