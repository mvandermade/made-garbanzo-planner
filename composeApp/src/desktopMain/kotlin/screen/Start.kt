package screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import model.AppState
import model.Prefs
import screen.preferences.ProfilesColumn
import screen.start.DatePickerRow
import writeAndOpenPdfToTemp
import java.util.prefs.Preferences

@Composable
fun start(
    requestNewAppState: (appState: AppState) -> Unit,
    prefs: Preferences,
) {
    // To let the user see the change immediately
    val activeProfile = remember { mutableStateOf(prefs.get(Prefs.ACTIVE_PROFILE.key, "0")) }

    MaterialTheme {
        ProfilesColumn(prefs, activeProfile)
        Column {
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { requestNewAppState(AppState.PREFERENCES) }) {
                        Text("Ga naar instellingen ⚙️")
                    }
                }
            }
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { writeAndOpenPdfToTemp(prefs) }) {
                        Text("Genereer PDF 📜(of druk op ENTER)")
                    }
                }
            }
            Divider()
            DatePickerRow(prefs)
        }
    }
}
