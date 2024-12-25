package preferences

import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.json.Json
import model.preferences.PreferencesTree
import model.preferences.PreferencesV1
import java.util.prefs.Preferences

private val INITIAL_JSON =
    """
    { "version": 0 }
    """.trimIndent()

class PreferencesStoreRaw(private val preferences: Preferences) {
    val version: Long

    private val jsonIgnoreUnknownKeys = Json { ignoreUnknownKeys = true }
    private val json = Json

    init {
        version =
            jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(
                preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON),
            ).version
    }

    fun dumpAsString(preferencesTree: String) {
        // Verify there is at least a version present
        jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(preferencesTree)
        preferences.put(APP_PREFERENCES_JSON, preferencesTree)
    }

    fun readAsString(): String {
        // TODO It might be bad to return an empty thing here?
        return preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON)
    }

    fun readV1(): PreferencesV1 {
        return jsonIgnoreUnknownKeys.decodeFromString<PreferencesV1>(
            preferences.get(APP_PREFERENCES_JSON, INITIAL_JSON),
        )
    }
}
