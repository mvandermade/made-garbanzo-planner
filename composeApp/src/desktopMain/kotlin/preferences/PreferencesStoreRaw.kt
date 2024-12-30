package preferences

import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.json.Json
import models.preferences.PreferencesTree
import models.preferences.PreferencesV1
import java.util.prefs.Preferences

private val INITIAL_JSON =
    """
    { "version": 0 }
    """.trimIndent()

class PreferencesStoreRaw(private val preferences: Preferences) {
    private val jsonIgnoreUnknownKeys = Json { ignoreUnknownKeys = true }
    private val json = Json

    fun readVersion(): Long {
        return jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(
            preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON),
        ).version
    }

    fun dumpAsString(preferencesTree: String) {
        // Verify there is at least a version present
        jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(preferencesTree)
        preferences.put(APP_PREFERENCES_JSON, preferencesTree)
    }

    fun resetToDefault() {
        preferences.remove(APP_PREFERENCES_JSON)
    }

    fun readAsStringIfExists(): String {
        return preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON)
    }

    fun readV1(): PreferencesV1 {
        return json.decodeFromString<PreferencesV1>(
            preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON),
        )
    }
}
