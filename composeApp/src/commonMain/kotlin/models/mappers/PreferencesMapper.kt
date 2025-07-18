package models.mappers

import models.preferences.PreferencesV1
import models.preferences.PreferencesV2

fun PreferencesV1.toV2(
    externalPreferencesIsEnabled: Boolean,
    externalPreferencesPath: String,
): PreferencesV2 =
    PreferencesV2(
        version = version,
        rruleSets = rruleSets,
        activeProfile = activeProfile,
        onStartUpOpenPDF = onStartUpOpenPDF,
        profiles = profiles,
        startDate = startDate,
        startDateIsEnabled = startDateIsEnabled,
        autoOpenPDFAfterGenerationIsEnabled = autoOpenPDFAfterGenerationIsEnabled,
        pdfOutputPath = pdfOutputPath,
        externalPreferencesIsEnabled = externalPreferencesIsEnabled,
        externalPreferencesPath = externalPreferencesPath,
    )
