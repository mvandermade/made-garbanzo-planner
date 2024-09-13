package screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import model.AppState
import writeAndOpenPdfToTemp
import java.util.prefs.Preferences

@Composable
fun start(
    requestNewAppState: (appState: AppState) -> Unit,
    prefs: Preferences,
) {
    MaterialTheme {
        Column {
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { requestNewAppState(AppState.PREFERENCES) }) {
                        Text("Go to preferences")
                    }
                }
            }
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { writeAndOpenPdfToTemp(prefs) }) {
                        Text("Generate PDF (or press ENTER)")
                    }
                }
            }
        }
    }
}
