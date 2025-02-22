package screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import models.AppState
import repositories.PreferencesStore
import screens.preferences.ProfilesColumn
import screens.preferences.RRuleSetRows

@Composable
fun preferences(
    requestNewAppState: (appState: AppState) -> Unit,
    preferencesStore: PreferencesStore,
) {
    // To let the user see the change immediately
    val activeProfile = remember { mutableStateOf(preferencesStore.activeProfile) }

    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ProfilesColumn(preferencesStore, activeProfile)
            Column {
                Row {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { requestNewAppState(AppState.START) }) {
                            Text("Ga terug naar start")
                        }
                        Button(onClick = { requestNewAppState(AppState.ADVANCED_PREFERENCES) }) {
                            Text("Geavanceerde instellingen")
                        }
                    }
                }
                RRuleSetRows(preferencesStore, activeProfile)
            }
        }
    }
}
