package preferences

import preferences.changesets.addPreferences
import preferences.changesets.addSportProfile
import preferences.changesets.initialData

fun migratePreferences(preferencesStore: PreferencesStoreRaw) {
    val currentVersion = preferencesStore.readVersion()

    1L.let { if (currentVersion <= it) initialData(it, preferencesStore) }
    2L.let { if (currentVersion <= it) addSportProfile(it, preferencesStore) }
    3L.let { if (currentVersion <= it) addPreferences(it, preferencesStore) }
    // Latest written version is v4
}
