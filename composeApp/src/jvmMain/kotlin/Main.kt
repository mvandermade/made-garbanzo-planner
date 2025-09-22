import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import exceptions.PreferenceExceptionScreen
import pdfhelpers.writeAndOpenMainDocument
import preferences.PreferencesStoreInternal
import preferences.store.ObtainedStoreResult
import preferences.store.obtainStore
import repositories.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val javaPreferences = Preferences.userRoot().node(PreferencesStoreInternal::class.java.name)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner ${System.getProperty("jpackage.app-version") ?: ""}",
        ) {
            when (val result = obtainStore(javaPreferences)) {
                is ObtainedStoreResult.Ready -> {
                    preflight(result.store)
                    App(result.store)
                }
                is ObtainedStoreResult.Error -> PreferenceExceptionScreen(result.store)
            }
        }
    }
}

fun preflight(store: PreferencesStore) {
    // Flip flags on startup to clean up unwanted behavior
    if (store.startDateIsEnabled) store.startDateIsEnabled = false

    if (store.onStartUpOpenPDF) {
        try {
            // Path is used for CLI access and tests
            store.pdfOutputPath = writeAndOpenMainDocument(store)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
