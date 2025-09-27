package preferences

import models.preferences.PreferencesV2

interface PreferencesStoreRaw {
    fun readVersion(): Long

    fun dumpAsString(preferencesTree: String)

    fun resetToDefault()

    fun readAsStringIfExists(): String

    fun readV2(): PreferencesV2
}
