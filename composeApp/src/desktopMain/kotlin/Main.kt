import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import exceptions.PreferenceExceptionScreen
import preferences.PreferencesStoreRaw
import preferences.migratePreferences
import repositories.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val preferences = Preferences.userRoot().node(PreferencesStoreRaw::class.java.name)
    val preferencesStoreRaw = PreferencesStoreRaw(preferences)

    application {
        Window(
            icon = painterResource("icon.png"),
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner ${System.getProperty("jpackage.app-version") ?: ""}",
        ) {
            try {
                migratePreferences(preferencesStoreRaw)
            } catch (e: Exception) {
                PreferenceExceptionScreen(preferencesStoreRaw)
            }

            val preferencesV1 =
                try {
                    preferencesStoreRaw.readV1()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

            if (preferencesV1 == null) {
                PreferenceExceptionScreen(preferencesStoreRaw)
            } else {
                val preferencesStore = PreferencesStore(preferences, preferencesV1)

                if (preferencesStore.onStartUpOpenPDF) {
                    preferencesStore.pdfOutputPath = writeAndOpenMainDocument(preferencesStore)
                }
                App(preferencesStore)
            }
        }
    }
}
