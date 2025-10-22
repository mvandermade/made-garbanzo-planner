package preferences.changesets

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ProfileV1
import models.preferences.PreferencesV1
import preferences.PreferencesStoreRaw

fun addSportProfile(
    version: Long,
    preferencesStore: PreferencesStoreRaw,
) {
    val treeV1 =
        Json.decodeFromString<PreferencesV1>(
            preferencesStore.readAsStringIfExists(),
        )
    treeV1.profiles += ProfileV1(3, "Sport")

    preferencesStore.dumpAsString(
        Json.encodeToString(treeV1),
    )

    bumpVersion(version, preferencesStore)
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
