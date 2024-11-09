package screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Divider
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    var pdfPath by remember { mutableStateOf("") }

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
                    Button(onClick = {
                        pdfPath = writeAndOpenPdfToTemp(prefs)
                    }) {
                        Text("Genereer PDF 📜(of druk op ENTER)")
                    }
                }
            }
            if (!prefs.getBoolean(Prefs.AUTO_OPEN_PDF.key, true)) {
                // For now this is used for testing only, maybe some users will like it too after setting it
                Divider()
                Row {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(
                            label = { Text("pdfPath") },
                            value = pdfPath,
                            onValueChange = {},
                            modifier =
                                Modifier.semantics {
                                    contentDescription = "pdfPath"
                                },
                        )
                    }
                }
            }
            Divider()
            DatePickerRow(prefs)
        }
    }
}
