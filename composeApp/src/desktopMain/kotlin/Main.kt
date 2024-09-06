import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner",
            onKeyEvent = {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    writeAndOpenPdfToTemp()
                    true
                } else {
                    false
                }
            },
        ) {
            App()
        }
    }
