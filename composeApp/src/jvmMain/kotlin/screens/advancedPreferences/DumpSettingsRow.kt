package screens.advancedPreferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import repositories.PreferencesStore
import java.io.File

@Composable
fun DumpSettingsRow(preferencesStore: PreferencesStore) {
    var dumpPath by remember { mutableStateOf("") }

    Row {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dump maken van de instellingen:")
                TextField(
                    label = { Text("Pad om te schrijven (*.json)") },
                    value = dumpPath,
                    onValueChange = { dumpPath = it },
                )
                Button(onClick = { writePreferencesToDisk(dumpPath, preferencesStore) }) {
                    Text("Schrijf json")
                }
            }
        }
    }
}

fun writePreferencesToDisk(
    path: String,
    preferencesStore: PreferencesStore,
) {
    File(path).writeText(preferencesStore.getPreferencesAsJson())
}
