import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle

fun getDocumentOf(page: PDPage): PDDocument {
    val document = PDDocument()
    document.addPage(page)
    return document
}

fun getRotatedA4Page(): PDPage {
    val page =
        PDPage(
            PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width),
        )
    return page
}

fun getContentStream(
    doc: PDDocument,
    page: PDPage,
) = PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false, true)
