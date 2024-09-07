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
import kotlin.math.floor

fun getPdf(page: PDPage): PDDocument {
    val document = PDDocument()
    document.addPage(page)
    return document
}

fun getPage(): PDPage {
    val page =
        PDPage(
            PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width),
        )
    return page
}

fun savePdf(
    document: PDDocument,
    file: File,
) {
    document.save(file)
}

fun writeText(
    doc: PDDocument,
    page: PDPage,
    xOffset: Float,
    yOffset: Float,
    text: String,
    font: PDFont,
    fontSize: Float,
    draw: Boolean = true,
): BoxCoordinates {
    val titleWidth = (font.getStringWidth(text) / 1000) * fontSize
    val titleCapHeight = (font.fontDescriptor.capHeight / 1000) * fontSize
    // Get descender height characters such as p and q
    val titleDescent = font.fontDescriptor.descent / 1000 * fontSize

    if (draw) {
        getContentStream(doc, page).use { contents ->
            contents.beginText()
            contents.setFont(font, fontSize)
            contents.newLineAtOffset(xOffset, yOffset)
            contents.showText(text)
            contents.endText()
            contents.stroke()
        }
    }
    // Note because of the yOffset the rectangle starts with the offset
    return BoxCoordinates(
        xOffset,
        // The font is as high as the cap height
        yOffset + titleCapHeight,
        // The amount of characters times box width
        xOffset + titleWidth,
        // Take into account the descenders such as p and q (negative value)
        yOffset + titleDescent,
    )
}

fun doFirstColumn(
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
        drawBox(doc, page, BoxCoordinates(bc.topLeftX, bc.bottomRightY, bc.bottomRightX, yOffset))
    }
    return BoxCoordinates(topLeftX, topLeftY, bc.bottomRightX, yOffset)
}

fun doSecondColumn(
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
        drawBox(doc, page, BoxCoordinates(bc.topLeftX, bc.bottomRightY, bc.bottomRightX, yOffset))
    }

    return BoxCoordinates(topLeftX, topLeftY, bc.bottomRightX, yOffset)
}

fun drawBox(
    doc: PDDocument,
    page: PDPage,
    coordinates: BoxCoordinates,
) {
    // The coordinates start at the top left. But drawing a box starts at the bottom left.
    val width = coordinates.bottomRightX - coordinates.topLeftX
    val height = coordinates.topLeftY - coordinates.bottomRightY

    getContentStream(doc, page).use { contents ->
        contents.setLineWidth(0.5f)
        contents.addRect(
            coordinates.topLeftX,
            coordinates.bottomRightY,
            width,
            height,
        )
        contents.stroke()
    }
}

fun writeCell(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    cellWidth: Float,
    cellHeight: Float,
    draw: Boolean = true,
): BoxCoordinates {
    val bottomLeftY = topLeftY - cellHeight

    if (draw) {
        getContentStream(doc, page).use { contents ->
            contents.setLineWidth(0.5f)
            contents.addRect(topLeftX, bottomLeftY, cellWidth, cellHeight)
            contents.stroke()
        }
    }

    return BoxCoordinates(topLeftX, topLeftY, topLeftX + cellWidth, bottomLeftY)
}

fun writeTextAndCell(
    doc: PDDocument,
    page: PDPage,
    topLeftX: Float,
    topLeftY: Float,
    content: String,
    cellWidth: Float = 100f,
    cellHeight: Float = 20f,
    font: PDFont = PDType1Font(Standard14Fonts.FontName.HELVETICA),
    fontSize: Float = 12f,
    draw: Boolean = true,
): BoxCoordinates {
    val bottomLeftY = topLeftY - cellHeight
    val titleCapHeight = (font.fontDescriptor.xHeight / 1000) * fontSize
    val contentWidth = (font.getStringWidth(content) / 1000) * fontSize

    writeCell(doc, page, topLeftX, topLeftY, cellWidth, cellHeight, draw)

    if (draw) {
        getContentStream(doc, page).use { contents ->
            contents.beginText()
            contents.newLineAtOffset(
                topLeftX + cellWidth / 2 - contentWidth / 2,
                bottomLeftY + cellHeight / 2 - (titleCapHeight) / 2,
            )
            contents.setFont(font, fontSize)
            contents.showText(content)
            contents.endText()
            contents.stroke()
        }
    }

    return BoxCoordinates(topLeftX, topLeftY, topLeftX + cellWidth, topLeftY - cellHeight)
}

data class BoxCoordinates(val topLeftX: Float, val topLeftY: Float, val bottomRightX: Float, val bottomRightY: Float)

data class BoxCoordinatesHolder(
    var topLeftX: Float,
    var topLeftY: Float,
    var bottomRightX: Float,
    var bottomRightY: Float,
)

fun BoxCoordinatesHolder.toBoxCoordinates(): BoxCoordinates =
    BoxCoordinates(topLeftX, topLeftY, bottomRightX, bottomRightY)

fun getContentStream(
    doc: PDDocument,
    page: PDPage,
) = PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)
