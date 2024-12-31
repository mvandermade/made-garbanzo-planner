import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import exceptions.PreferenceExceptionScreen
import preferences.PreferencesStoreExternal
import preferences.PreferencesStoreInternal
import preferences.migratePreferences
import repositories.PreferencesStore
import java.util.prefs.Preferences

fun main() {
    val preferences = Preferences.userRoot().node(PreferencesStoreInternal::class.java.name)
    val preferencesStoreInternal = PreferencesStoreInternal(preferences)

    application {
        Window(
            icon = painterResource("icon.png"),
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner ${System.getProperty("jpackage.app-version") ?: ""}",
        ) {
            try {
                migratePreferences(preferencesStoreInternal)
            } catch (e: Exception) {
                PreferenceExceptionScreen(preferencesStoreInternal)
            }

            val preferencesV2 =
                try {
                    preferencesStoreInternal.readV2()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

            if (preferencesV2 == null) {
                PreferenceExceptionScreen(preferencesStoreInternal)
            } else {
                var preferencesStore = PreferencesStore(preferences, preferencesV2)

                if (preferencesStore.externalPreferencesIsEnabled) {
                    val externalPreferencesPath = preferencesStore.externalPreferencesPath
                    val preferencesStoreExternal =
                        PreferencesStoreExternal(preferences, preferencesStore.externalPreferencesPath)
                    try {
                        migratePreferences(preferencesStoreExternal)
                    } catch (e: Exception) {
                        preferencesStore.externalPreferencesIsEnabled = false
                        throw e
                    }

                    val preferencesV2external =
                        try {
                            preferencesStoreExternal.readV2()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }

                    if (preferencesV2external == null) {
                        PreferenceExceptionScreen(preferencesStoreInternal)
                    } else {
                        preferencesStore = PreferencesStore(preferences, preferencesV2external)
                        // Set the proper paths that might be missing due to a lower version or new file...
                        preferencesStore.externalPreferencesIsEnabled = true
                        preferencesStore.externalPreferencesPath = externalPreferencesPath
                    }
                }

                if (preferencesStore.onStartUpOpenPDF) {
                    preferencesStore.pdfOutputPath = writeAndOpenMainDocument(preferencesStore)
                }
                App(preferencesStore)
            }
        }
    }
}
