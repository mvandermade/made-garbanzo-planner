package screens.advancedPreferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
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
import preferences.INITIAL_JSON
import repositories.PreferencesStore
import java.io.File
import kotlin.system.exitProcess

@Composable
fun ExternalPreferencesRow(preferencesStore: PreferencesStore) {
    var externalPreferencesIsEnabled by remember { mutableStateOf(preferencesStore.externalPreferencesIsEnabled) }
    var externalPreferencesPath by remember { mutableStateOf(preferencesStore.externalPreferencesPath) }

    Row {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(10.dp)) {}
                Column(Modifier.width(400.dp)) {
                    Text("Eigenschappen bewaren in extern bestand? Uitschakelen sluit het programma.")
                }
                Checkbox(
                    checked = externalPreferencesIsEnabled,
                    onCheckedChange = {
                        if (!it) prepareInternalAttachment(preferencesStore)
                        externalPreferencesIsEnabled = it
                    },
                )
            }
            if (externalPreferencesIsEnabled) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.width(10.dp)) {}
                    Column(Modifier.width(400.dp)) {}
                    TextField(
                        label = { Text("Pad extern bestand om te lezen/schrijven(*.json)") },
                        value = externalPreferencesPath,
                        onValueChange = { externalPreferencesPath = it },
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.width(10.dp)) {}
                    Column(Modifier.width(400.dp)) {}
                    Button(onClick = { prepareExternalAttachment(preferencesStore, externalPreferencesPath) }) {
                        Text("Bevestig ( + sluit programma af)")
                    }
                }
            }
        }
    }
}

fun prepareExternalAttachment(
    preferencesStore: PreferencesStore,
    preferencesPath: String,
) {
    if (!File(preferencesPath).exists()) {
        File(preferencesPath).writeText(INITIAL_JSON)
    }
    preferencesStore.externalPreferencesPath = preferencesPath
    preferencesStore.externalPreferencesIsEnabled = true
    // Stop the application because cannot do migrations now.
    exitProcess(0)
}

fun prepareInternalAttachment(preferencesStore: PreferencesStore) {
    preferencesStore.externalPreferencesPath = ""
    preferencesStore.externalPreferencesIsEnabled = false
    // Stop the application because cannot do migrations now.
    exitProcess(0)
}
