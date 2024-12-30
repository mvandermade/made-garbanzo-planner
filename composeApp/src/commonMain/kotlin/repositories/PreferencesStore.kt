package repositories

import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ProfileV1
import models.RRuleSetV1
import models.preferences.PreferencesV1
import java.util.prefs.Preferences

class PreferencesStore(
    private val preferences: Preferences,
    private val prefs: PreferencesV1,
) {
    private val json = Json

    private fun persistPrefs() {
        preferences.put(APP_PREFERENCES_JSON, json.encodeToString(prefs))
    }

    var rruleSets: Set<RRuleSetV1>
        get() = prefs.rruleSets
        set(rruleSets) {
            prefs.rruleSets = rruleSets
            persistPrefs()
        }

    var activeProfile: Long
        get() = prefs.activeProfile
        set(activeProfile) {
            prefs.activeProfile = activeProfile
            persistPrefs()
        }

    var onStartUpOpenPDF: Boolean
        get() = prefs.onStartUpOpenPDF
        set(onStartUpOpenPDF) {
            prefs.onStartUpOpenPDF = onStartUpOpenPDF
            persistPrefs()
        }
    var profiles: Set<ProfileV1>
        get() = prefs.profiles
        set(profiles) {
            prefs.profiles = profiles
            persistPrefs()
        }
    var startDate: String
        get() = prefs.startDate
        set(startDate) {
            prefs.startDate = startDate
            persistPrefs()
        }
    var startDateIsEnabled: Boolean
        get() = prefs.startDateIsEnabled
        set(startDateIsEnabled) {
            prefs.startDateIsEnabled = startDateIsEnabled
            persistPrefs()
        }
    var autoOpenPDFAfterGenerationIsEnabled: Boolean
        get() = prefs.autoOpenPDFAfterGenerationIsEnabled
        set(autoOpenPDFAfterGenerationIsEnabled) {
            prefs.autoOpenPDFAfterGenerationIsEnabled = autoOpenPDFAfterGenerationIsEnabled
            persistPrefs()
        }

    var pdfOutputPath: String
        get() = prefs.pdfOutputPath
        set(pdfOutputPath) {
            prefs.pdfOutputPath = pdfOutputPath
            persistPrefs()
        }
}
