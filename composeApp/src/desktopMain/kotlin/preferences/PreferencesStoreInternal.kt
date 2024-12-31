package preferences

import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.json.Json
import models.preferences.PreferencesTree
import models.preferences.PreferencesV2
import java.util.prefs.Preferences

val INITIAL_JSON =
    """
    { "version": 0 }
    """.trimIndent()

class PreferencesStoreInternal(private val preferences: Preferences) : PreferencesStoreRaw {
    private val jsonIgnoreUnknownKeys = Json { ignoreUnknownKeys = true }
    private val json = Json

    override fun readVersion(): Long {
        return jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(
            preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON),
        ).version
    }

    override fun dumpAsString(preferencesTree: String) {
        // Verify there is at least a version present
        jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(preferencesTree)
        preferences.put(APP_PREFERENCES_JSON, preferencesTree)
    }

    override fun resetToDefault() {
        preferences.remove(APP_PREFERENCES_JSON)
    }

    override fun readAsStringIfExists(): String {
        return preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON)
    }

    override fun readV2(): PreferencesV2 {
        return json.decodeFromString<PreferencesV2>(
            preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON),
        )
    }
}
