package screen

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
import model.AppState
import model.Prefs
import screen.preferences.AutoLaunchRow
import screen.preferences.ProfilesColumn
import screen.preferences.RRuleSetRows
import java.util.prefs.Preferences

@Composable
fun preferences(
    requestNewAppState: (appState: AppState) -> Unit,
    prefs: Preferences,
) {
    // To let the user see the change immediately
    val activeProfile = remember { mutableStateOf(prefs.get(Prefs.ACTIVE_PROFILE.key, "0")) }

    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ProfilesColumn(prefs, activeProfile)
            Column {
                Row {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { requestNewAppState(AppState.START) }) {
                            Text("Ga terug naar start")
                        }
                    }
                }
                AutoLaunchRow(prefs)
                RRuleSetRows(prefs, activeProfile)
            }
        }
    }
}
