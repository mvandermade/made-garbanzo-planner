import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Desktop
import java.io.File
import java.nio.file.Files

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { writePDFToDisk() }) {
                Text("Generate PDF")
            }
        }
    }
}

fun writePDFToDisk() {
    val tempFile = kotlin.io.path.createTempFile("planner-101-", suffix = ".pdf")
    val pdf = getPdf()
    tempFile.toFile().deleteOnExit();
    savePdf(pdf, tempFile.toFile())
    Desktop.getDesktop().open(tempFile.toFile())
}
