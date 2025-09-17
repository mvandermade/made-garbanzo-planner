import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import exceptions.PreferenceExceptionScreen
import models.preferences.PreferencesV2
import pdf.writeAndOpenMainDocument
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
            onCloseRequest = ::exitApplication,
            title = "made-garbanzo-planner ${System.getProperty("jpackage.app-version") ?: ""}",
        ) {
            val migrationSuccess =
                try {
                    migratePreferences(preferencesStoreInternal)
                    true
                } catch (e: Exception) {
                    println("Migration failed: ${e.message}")
                    e.printStackTrace()
                    false
                }

            if (migrationSuccess) {
                val preferencesV2internal =
                    try {
                        preferencesStoreInternal.readV2()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                var preferencesV2external: PreferencesV2? = null
                var preferencesStoreExternal: PreferencesStoreExternal? = null

                if (preferencesV2internal != null) {
                    var preferencesStore = PreferencesStore(preferences, preferencesV2internal)

                    if (preferencesStore.externalPreferencesIsEnabled) {
                        val externalPreferencesPath = preferencesStore.externalPreferencesPath
                        preferencesStoreExternal =
                            PreferencesStoreExternal(preferences, preferencesStore.externalPreferencesPath)
                        try {
                            migratePreferences(preferencesStoreExternal)
                        } catch (e: Exception) {
                            preferencesStore.externalPreferencesIsEnabled = false
                            throw e
                        }

                        preferencesV2external =
                            try {
                                preferencesStoreExternal.readV2()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }

                        if (preferencesV2external == null) {
                            PreferenceExceptionScreen(preferencesStoreExternal)
                        } else {
                            preferencesStore = PreferencesStore(preferences, preferencesV2external)
                            // Set the proper paths that might be missing due to a lower version or new file...
                            preferencesStore.externalPreferencesIsEnabled = true
                            preferencesStore.externalPreferencesPath = externalPreferencesPath
                        }
                    }

                    if (preferencesStore.startDateIsEnabled) {
                        preferencesStore.startDateIsEnabled = false
                    }

                    if (preferencesStore.onStartUpOpenPDF) {
                        preferencesStore.pdfOutputPath = writeAndOpenMainDocument(preferencesStore)
                    }

                    // Error handling
                    if (preferencesV2external == null && preferencesStoreExternal != null) {
                        PreferenceExceptionScreen(preferencesStoreExternal)
                    } else {
                        App(preferencesStore)
                    }
                } else {
                    PreferenceExceptionScreen(preferencesStoreInternal)
                }
            } else {
                PreferenceExceptionScreen(preferencesStoreInternal)
            }
        }
    }
}
