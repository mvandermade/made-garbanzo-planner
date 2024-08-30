import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import java.io.File

fun getPdf(): PDDocument {
    val document = PDDocument()
    document.addPage(PDPage())
    return document
}

fun savePdf(document: PDDocument, file: File) {
    document.save(file)
}
