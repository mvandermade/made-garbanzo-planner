package exceptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import preferences.PreferencesStoreInternal
import kotlin.system.exitProcess

@Composable
fun PreferenceExceptionScreen(preferencesStoreInternal: PreferencesStoreInternal) {
    MaterialTheme {
        Column {
            Row {
                Text(
                    "Je configuratie kon niet worden gelezen. Geen idee waar de fout precies zit helaas." +
                        " Hieronder de dump. Reset naar fabrieksinstellingen? Je kunt later de backup terugzetten" +
                        " door de json hieronder te kopieren en te corrigeren:",
                )
            }
            Row {
                Button(
                    onClick = {
                        preferencesStoreInternal.resetToDefault()
                        exitProcess(0)
                    },
                ) {
                    Text("RESET alle gegevens en sluit af")
                }
            }
            Row {
                TextField(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    value = preferencesStoreInternal.readAsStringIfExists(),
                    onValueChange = {},
                    readOnly = false,
                    enabled = true,
                )
            }
        }
    }
}
