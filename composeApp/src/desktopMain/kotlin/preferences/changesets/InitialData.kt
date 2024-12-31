package preferences.changesets

import formatterLDT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ProfileV1
import models.RRuleSetV1
import models.preferences.PreferencesV1
import preferences.PreferencesStoreRaw
import java.time.LocalDateTime

fun initialData(
    version: Long,
    preferencesStoreInternal: PreferencesStoreRaw,
) {
    initializePreferences(version, preferencesStoreInternal)
    bumpVersion(version, preferencesStoreInternal)
}

private fun initializePreferences(
    version: Long,
    preferencesStoreInternal: PreferencesStoreRaw,
) {
    val treeV1 =
        PreferencesV1(
            version = version,
            rruleSets =
                setOf(
                    RRuleSetV1(
                        1L,
                        1,
                        "Dagelijks herhalen",
                        "FREQ=DAILY",
                        LocalDateTime.now().format(formatterLDT),
                    ),
                ),
            activeProfile = 1,
            onStartUpOpenPDF = true,
            profiles =
                setOf(
                    ProfileV1(
                        id = 1,
                        name = "Thuis",
                    ),
                    ProfileV1(
                        id = 2,
                        name = "Werk",
                    ),
                ),
            startDate = "01-01-2000",
            startDateIsEnabled = false,
            autoOpenPDFAfterGenerationIsEnabled = true,
            pdfOutputPath = "",
        )

    preferencesStoreInternal.dumpAsString(
        Json.encodeToString(treeV1),
    )
}

private fun bumpVersion(
    version: Long,
    preferencesStore: PreferencesStoreRaw,
) {
    val treeV1 =
        Json.decodeFromString<PreferencesV1>(
            preferencesStore.readAsStringIfExists(),
        )

    treeV1.version = version + 1

    preferencesStore.dumpAsString(
        Json.encodeToString(treeV1),
    )
}
