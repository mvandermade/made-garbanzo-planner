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
import repositories.PreferencesStore

@Composable
fun ProfilesColumn(
    preferencesStore: PreferencesStore,
    activeProfile: MutableState<Long>,
) {
    val profilesSet by remember {
        mutableStateOf(
            preferencesStore.profiles,
        )
    }

    Column(Modifier.fillMaxWidth(0.5f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            profilesSet.map { profile ->
                RadioButton(selected = activeProfile.value == profile.id, onClick = {
                    preferencesStore.activeProfile = profile.id
                    activeProfile.value = profile.id
                })
                ClickableText(AnnotatedString(profile.name), onClick = {
                    preferencesStore.activeProfile = profile.id
                    activeProfile.value = profile.id
                })
            }
        }
    }
}
