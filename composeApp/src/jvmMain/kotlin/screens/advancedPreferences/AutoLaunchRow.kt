package screens.advancedPreferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import repositories.PreferencesStore

@Composable
fun AutoLaunchRow(preferencesStore: PreferencesStore) {
    var autoStart by remember { mutableStateOf(preferencesStore.onStartUpOpenPDF) }
    var autoOpen by remember { mutableStateOf(preferencesStore.autoOpenPDFAfterGenerationIsEnabled) }

    Row {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(10.dp)) {}
                Column(Modifier.width(400.dp)) {
                    Text("PDF Generator automatisch starten?")
                }
                Column {
                    Checkbox(
                        checked = autoStart,
                        onCheckedChange = {
                            preferencesStore.onStartUpOpenPDF = it
                            autoStart = it
                        },
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(10.dp)) {}
                Column(Modifier.width(400.dp)) {
                    Text("PDF na genereren openen?")
                }
                Column {
                    Checkbox(
                        checked = autoOpen,
                        onCheckedChange = {
                            preferencesStore.autoOpenPDFAfterGenerationIsEnabled = it
                            autoOpen = it
                        },
                        modifier =
                            Modifier.semantics {
                                contentDescription = "auto-open-pdf"
                            },
                    )
                }
            }
        }
    }
}
