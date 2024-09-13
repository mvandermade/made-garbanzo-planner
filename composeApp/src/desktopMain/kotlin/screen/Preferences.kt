package screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import model.AppState
import java.util.prefs.Preferences

@Composable
fun preferences(
    requestNewAppState: (appState: AppState) -> Unit,
    prefs: Preferences,
) {
    // To let the user see the change immediately
    var autoStart by remember { mutableStateOf(prefs.getBoolean("app.auto-launch", false)) }
    var rrule by remember { mutableStateOf(prefs.get("app.rrule", "")) }
    var rruleDescription by remember { mutableStateOf(prefs.get("app.rruleDescription", "")) }

    MaterialTheme {
        Column {
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { requestNewAppState(AppState.START) }) {
                        Text("Go to start")
                    }
                }
            }
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Auto-launch PDF generator")
                        Checkbox(
                            checked = autoStart,
                            onCheckedChange = {
                                autoStart = it
                                prefs.putBoolean("app.auto-launch", it)
                            },
                        )
                    }
                    Row {
                        TextField(
                            label = { Text("RRULEDescription") },
                            value = rruleDescription,
                            onValueChange = {
                                rruleDescription = it
                                prefs.put("app.rruleDescription", it)
                            },
                        )
                    }
                    Row {
                        TextField(
                            label = { Text("RRULE") },
                            value = rrule,
                            onValueChange = {
                                rrule = it
                                prefs.put("app.rrule", it)
                            },
                        )
                    }
                }
            }
        }
    }
}
