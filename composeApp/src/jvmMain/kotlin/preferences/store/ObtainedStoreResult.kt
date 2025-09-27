package preferences.store

import preferences.PreferencesStoreExternal
import preferences.PreferencesStoreInternal
import preferences.PreferencesStoreRaw
import repositories.PreferencesStore
import java.util.prefs.Preferences

sealed class ObtainedStoreResult {
    data class Ready(
        val store: PreferencesStore,
    ) : ObtainedStoreResult()

    data class Error(
        val store: PreferencesStoreRaw,
    ) : ObtainedStoreResult()
}

fun obtainStore(javaPreferences: Preferences): ObtainedStoreResult {
    val internalStore = PreferencesStoreInternal(javaPreferences)
    try {
        migratePreferences(internalStore)
    } catch (e: Exception) {
        e.printStackTrace()
        return ObtainedStoreResult.Error(internalStore)
    }

    // Read internal to the latest object
    val internalPreferencesV2 =
        try {
            internalStore.readV2()
        } catch (e: Exception) {
            e.printStackTrace()
            return ObtainedStoreResult.Error(internalStore)
        }

    // Start with internal preferences
    var activeStore = PreferencesStore(javaPreferences, internalPreferencesV2)

    // If external is enabled, try to migrate and read external; on any failure show an error for external
    if (activeStore.externalPreferencesIsEnabled) {
        val externalPath = activeStore.externalPreferencesPath
        val externalStore = PreferencesStoreExternal(javaPreferences, externalPath)

        try {
            migratePreferences(externalStore)
        } catch (e: Exception) {
            // Disable external and show error for external
            activeStore.externalPreferencesIsEnabled = false
            e.printStackTrace()
            return ObtainedStoreResult.Error(externalStore)
        }

        // Read external to object
        val externalV2 =
            try {
                externalStore.readV2()
            } catch (e: Exception) {
                // Disable external and show error for external
                activeStore.externalPreferencesIsEnabled = false
                e.printStackTrace()
                return ObtainedStoreResult.Error(externalStore)
            }

        // Use external preferences; ensure flags are set
        activeStore = PreferencesStore(javaPreferences, externalV2)
        activeStore.externalPreferencesIsEnabled = true
        activeStore.externalPreferencesPath = externalPath
    }

    return ObtainedStoreResult.Ready(activeStore)
}
