package preferences

import preferences.changesets.addSportProfile
import preferences.changesets.initialData

fun migratePreferences(preferencesStoreRaw: PreferencesStoreRaw) {
    val currentVersion = preferencesStoreRaw.readVersion()

    1L.let { if (currentVersion <= it) initialData(it, preferencesStoreRaw) }
    2L.let { if (currentVersion <= it) addSportProfile(it, preferencesStoreRaw) }
}
