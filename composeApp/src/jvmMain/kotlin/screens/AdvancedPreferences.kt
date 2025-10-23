package screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.AppState
import repositories.PreferencesStore
import screens.advancedPreferences.AutoLaunchRow
import screens.advancedPreferences.DumpSettingsRow
import screens.advancedPreferences.ExternalPreferencesRow

@Composable
fun advancedPreferences(
    requestNewAppState: (appState: AppState) -> Unit,
    preferencesStore: PreferencesStore,
) {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column {
                Row {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { requestNewAppState(AppState.PREFERENCES) }, Modifier.width(250.dp)) {
                            Text("Ga terug naar instellingen")
                        }
                    }
                }
                AutoLaunchRow(preferencesStore)
                DumpSettingsRow(preferencesStore)
                ExternalPreferencesRow(preferencesStore)
            }
        }
    }
}
