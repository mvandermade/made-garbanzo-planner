package preferences.changesets

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ProfileV1
import model.preferences.PreferencesV1
import preferences.PreferencesStoreRaw

fun addSportProfile(
    version: Long,
    preferencesStoreRaw: PreferencesStoreRaw,
) {
    val treeV1 =
        Json.decodeFromString<PreferencesV1>(
            preferencesStoreRaw.readAsString(),
        )
    treeV1.profiles += ProfileV1(3, "Sport")

    preferencesStoreRaw.dumpAsString(
        Json.encodeToString(treeV1),
    )

    bumpVersion(version, preferencesStoreRaw)
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