package preferences.changesets

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.mappers.toV2
import models.preferences.PreferencesV1
import preferences.PreferencesStoreRaw

fun addExternalPreferences(
    version: Long,
    preferencesStore: PreferencesStoreRaw,
) {
    val treeV1 =
        Json.decodeFromString<PreferencesV1>(
            preferencesStore.readAsStringIfExists(),
        )

    val treeV2 = treeV1.toV2(false, "")

    treeV2.version = version + 1

    preferencesStore.dumpAsString(
        Json.encodeToString(treeV2),
    )
}
