package model.preferences

import kotlinx.serialization.Serializable
import model.ProfileV1
import model.RRuleSetV1

@Serializable
class PreferencesV1(
    override var version: Long,
    var rruleSets: Set<RRuleSetV1>,
    var activeProfile: Long,
    var onStartUpOpenPDF: Boolean,
    var profiles: Set<ProfileV1>,
    var startDate: String,
    var startDateIsEnabled: Boolean,
    var autoOpenPDFAfterGenerationIsEnabled: Boolean,
    var pdfOutputPath: String,
) : IPreferences
