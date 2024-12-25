package model.preferences

import kotlinx.serialization.Serializable

@Serializable
class PreferencesTree(
    override var version: Long,
) : IPreferences
