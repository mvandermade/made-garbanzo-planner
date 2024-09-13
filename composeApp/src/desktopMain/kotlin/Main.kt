import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import preferences.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val prefs = Preferences.userRoot().node(PreferencesStore::class.java.name)

    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner",
            onKeyEvent = {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    writeAndOpenPdfToTemp(prefs)
                    true
                } else {
                    false
                }
            },
        ) {
            App(prefs)
            if (prefs.getBoolean("app.auto-launch", false)) {
                writeAndOpenPdfToTemp(prefs)
            }
        }
    }
}
