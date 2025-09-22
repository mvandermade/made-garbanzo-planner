import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import exceptions.PreferenceExceptionScreen
import pdf.writeAndOpenMainDocument
import preferences.PreferencesStoreExternal
import preferences.PreferencesStoreInternal
import preferences.PreferencesStoreRaw
import preferences.migratePreferences
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
                is StartupResult.AppReady -> App(result.store)
                is StartupResult.PreferenceError -> PreferenceExceptionScreen(result.store)
            }
        }
    }
}

sealed class StartupResult {
    data class AppReady(
        val store: PreferencesStore,
    ) : StartupResult()

    data class PreferenceError(
        val store: PreferencesStoreRaw,
    ) : StartupResult()
}

fun obtainStore(javaPreferences: Preferences): StartupResult {
    val internalStore = PreferencesStoreInternal(javaPreferences)

    // Migrate internal
    try {
        migratePreferences(internalStore)
    } catch (e: Exception) {
        e.printStackTrace()
        return StartupResult.PreferenceError(internalStore)
    }

    // Read internal to object
    val internalPreferencesV2 =
        try {
            internalStore.readV2()
        } catch (e: Exception) {
            e.printStackTrace()
            return StartupResult.PreferenceError(internalStore)
        }

    // Start with internal preferences
    var activeStore = PreferencesStore(javaPreferences, internalPreferencesV2)

    // If external is enabled, try to migrate and read external; on any failure show error for external
    if (activeStore.externalPreferencesIsEnabled) {
        val externalPath = activeStore.externalPreferencesPath
        val externalStore = PreferencesStoreExternal(javaPreferences, externalPath)

        // Migrate external
        try {
            migratePreferences(externalStore)
        } catch (e: Exception) {
            // Disable external and show error for external
            activeStore.externalPreferencesIsEnabled = false
            e.printStackTrace()
            return StartupResult.PreferenceError(externalStore)
        }

        // Read external to object
        val externalV2 =
            try {
                externalStore.readV2()
            } catch (e: Exception) {
                // Disable external and show error for external
                activeStore.externalPreferencesIsEnabled = false
                e.printStackTrace()
                return StartupResult.PreferenceError(externalStore)
            }

        // Use external preferences; ensure flags are set
        activeStore = PreferencesStore(javaPreferences, externalV2)
        activeStore.externalPreferencesIsEnabled = true
        activeStore.externalPreferencesPath = externalPath
    }

    // Flip flags on startup to cleanup unwanted behavior
    if (activeStore.startDateIsEnabled) activeStore.startDateIsEnabled = false

    // Optional PDF, the pdfoutput path is used for CLI access such as tests
    if (activeStore.onStartUpOpenPDF) {
        try {
            activeStore.pdfOutputPath = writeAndOpenMainDocument(activeStore)
        } catch (e: Exception) {
            // Ignore PDF errors at startup; keep app usable
            e.printStackTrace()
        }
    }

    return StartupResult.AppReady(activeStore)
}
