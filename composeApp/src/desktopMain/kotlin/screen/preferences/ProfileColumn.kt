package screen.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import kotlinx.serialization.json.Json
import model.Prefs
import model.Profile
import java.util.prefs.Preferences

@Composable
fun ProfilesColumn(
    prefs: Preferences,
    activeProfile: MutableState<String>,
) {
    val profilesString by remember {
        mutableStateOf(
            prefs.get(
                Prefs.PROFILES.key,
                """
                [{"id":1, "name":"Profile1"}, {"id":2, "name":"Profile2"}]
                """.trimIndent(),
            ),
        )
    }

    // Enforce the equals method
    val profilesSet =
        try {
            Json.decodeFromString<List<Profile>>(profilesString).toSet()
        } catch (e: Exception) {
            mutableSetOf()
        }

    Column(Modifier.fillMaxWidth(0.5f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            profilesSet.map { profile ->
                RadioButton(selected = activeProfile.value.toLong() == profile.id, onClick = {
                    activeProfile.value = profile.id.toString()
                    prefs.put(Prefs.ACTIVE_PROFILE.key, activeProfile.value)
                })
                ClickableText(AnnotatedString(profile.name), onClick = {
                    activeProfile.value = profile.id.toString()
                    prefs.put(Prefs.ACTIVE_PROFILE.key, activeProfile.value)
                })
            }
        }
    }
}
