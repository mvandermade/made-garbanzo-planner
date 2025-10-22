package pdfhelpers

import models.BoxCoordinates
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts

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

fun writeBox(
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
