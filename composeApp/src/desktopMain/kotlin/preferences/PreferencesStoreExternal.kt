package preferences

import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.json.Json
import models.preferences.PreferencesTree
import models.preferences.PreferencesV2
import java.io.File
import java.util.prefs.Preferences

class PreferencesStoreExternal(private val preferences: Preferences, private val path: String) : PreferencesStoreRaw {
    private val jsonIgnoreUnknownKeys = Json { ignoreUnknownKeys = true }
    private val json = Json

    override fun readVersion(): Long {
        return jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(
            File(path).readText(),
        ).version
    }

    override fun dumpAsString(preferencesTree: String) {
        // Verify there is at least a version present
        jsonIgnoreUnknownKeys.decodeFromString<PreferencesTree>(preferencesTree)
        File(path).writeText(preferencesTree)
    }

    override fun resetToDefault() {
        preferences.remove(APP_PREFERENCES_JSON)
    }

    override fun readAsStringIfExists(): String {
        return File(path).readText()
    }

    override fun readV2(): PreferencesV2 {
        return json.decodeFromString<PreferencesV2>(
            File(path).readText(),
        )
    }
}
