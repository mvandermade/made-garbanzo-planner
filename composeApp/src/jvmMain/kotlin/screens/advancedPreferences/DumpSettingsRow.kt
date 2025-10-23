package screens.advancedPreferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import repositories.PreferencesStore
import java.io.File

@Composable
fun DumpSettingsRow(preferencesStore: PreferencesStore) {
    var dumpPath by remember { mutableStateOf("") }

    Row {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(10.dp)) {}
                Column(Modifier.width(400.dp)) {
                    Text("Dump maken van de instellingen")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(10.dp)) {}
                Column(Modifier.width(400.dp)) { }
                TextField(
                    label = { Text("Pad om te schrijven (*.json)") },
                    value = dumpPath,
                    onValueChange = { dumpPath = it },
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(10.dp)) {}
                Column(Modifier.width(400.dp)) { }
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
