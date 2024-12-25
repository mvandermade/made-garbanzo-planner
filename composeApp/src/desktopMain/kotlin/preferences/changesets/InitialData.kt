package preferences.changesets

import formatterLDT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ProfileV1
import model.RRuleSetV1
import model.preferences.PreferencesV1
import preferences.PreferencesStoreRaw
import java.time.LocalDateTime

fun initialData(
    version: Long,
    preferencesStoreRaw: PreferencesStoreRaw,
) {
    initializePreferences(version, preferencesStoreRaw)
    bumpVersion(version, preferencesStoreRaw)
}

private fun initializePreferences(
    version: Long,
    preferencesStoreRaw: PreferencesStoreRaw,
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
        )

    preferencesStoreRaw.dumpAsString(
        Json.encodeToString(treeV1),
    )
}

private fun bumpVersion(
    version: Long,
    preferencesStoreRaw: PreferencesStoreRaw,
) {
    val treeV1 =
        Json.decodeFromString<PreferencesV1>(
            preferencesStoreRaw.readAsString(),
        )

    treeV1.version = version + 1

    preferencesStoreRaw.dumpAsString(
        Json.encodeToString(treeV1),
    )
}
