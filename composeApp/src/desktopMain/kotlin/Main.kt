import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Prefs
import preferences.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val prefs = Preferences.userRoot().node(PreferencesStore::class.java.name)

    // For some reason the application becomes buggy when you leave the option ON...
    prefs.putBoolean(Prefs.START_DATE_ENABLED.key, false)

    application {
        Window(
            icon = painterResource("icon.png"),
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
            if (prefs.getBoolean(Prefs.AUTO_LAUNCH.key, false)) {
                writeAndOpenPdfToTemp(prefs)
            }
        }
    }
}
