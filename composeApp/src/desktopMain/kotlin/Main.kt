import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import preferences.PreferencesStoreRaw
import preferences.migratePreferences
import repositories.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val preferences = Preferences.userRoot().node(PreferencesStoreRaw::class.java.name)

    application {
        Window(
            icon = painterResource("icon.png"),
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner ${System.getProperty("jpackage.app-version") ?: ""}",
        ) {
            val preferencesStoreRaw = PreferencesStoreRaw(preferences)
            migratePreferences(preferencesStoreRaw)
            val preferencesStore = PreferencesStore(preferences, preferencesStoreRaw.readV1())
            if (preferencesStore.onStartUpOpenPDF) {
                preferencesStore.pdfOutputPath = writeAndOpenMainDocument(preferencesStore)
            }
            App(preferencesStore)
        }
    }
}
