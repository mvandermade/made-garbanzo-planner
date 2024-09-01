import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
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
