package screens.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.onClick
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                val onClick = {
                    preferencesStore.activeProfile = profile.id
                    activeProfile.value = profile.id
                }

                RadioButton(selected = activeProfile.value == profile.id, onClick = onClick)
                Text(
                    profile.name,
                    Modifier.clickable {
                        onClick()
                    },
                )
            }
        }
    }
}
