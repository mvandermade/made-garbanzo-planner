package preferences.store

import preferences.PreferencesStoreRaw
import preferences.changesets.addExternalPreferences
import preferences.changesets.addSportProfile
import preferences.changesets.initialData

fun migratePreferences(preferencesStore: PreferencesStoreRaw) {
    val currentVersion = preferencesStore.readVersion()

    1L.let { if (currentVersion <= it) initialData(it, preferencesStore) }
    2L.let { if (currentVersion <= it) addSportProfile(it, preferencesStore) }
    3L.let { if (currentVersion <= it) addExternalPreferences(it, preferencesStore) }
    // The latest version written should be max + 1
}
