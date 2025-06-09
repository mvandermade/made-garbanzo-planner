package screens

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
import models.AppState
import repositories.PreferencesStore
import screens.preferences.ProfilesColumn
import screens.start.DatePickerRow
import pdf.writeAndOpenMainDocument

@Composable
fun start(
    requestNewAppState: (appState: AppState) -> Unit,
    preferencesStore: PreferencesStore,
) {
    val activeProfile = remember { mutableStateOf(preferencesStore.activeProfile) }
    var pdfPath by remember { mutableStateOf(preferencesStore.pdfOutputPath) }

    MaterialTheme {
        ProfilesColumn(preferencesStore, activeProfile)
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
                        pdfPath = writeAndOpenMainDocument(preferencesStore)
                    }) {
                        Text("Genereer PDF 📜")
                    }
                }
            }
            if (!preferencesStore.autoOpenPDFAfterGenerationIsEnabled) {
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
            DatePickerRow(preferencesStore)
        }
    }
}
