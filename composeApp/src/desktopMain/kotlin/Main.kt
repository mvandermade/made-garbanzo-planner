import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Prefs
import preferences.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val prefs = Preferences.userRoot().node(PreferencesStore::class.java.name)

    application {
        Window(
            icon = painterResource("icon.png"),
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner ${System.getProperty("jpackage.app-version") ?: ""}",
        ) {
            App(prefs)
            if (prefs.getBoolean(Prefs.ON_STARTUP_OPEN_PDF.key, false)) {
                writeAndOpenMainDocument(prefs)
            }
        }
    }
}
