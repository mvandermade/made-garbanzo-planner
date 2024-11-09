package screen.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import model.Prefs
import java.util.prefs.Preferences

@Composable
fun AutoLaunchRow(prefs: Preferences) {
    var autoStart by remember { mutableStateOf(prefs.getBoolean(Prefs.AUTO_LAUNCH.key, false)) }
    var autoOpen by remember { mutableStateOf(prefs.getBoolean(Prefs.AUTO_OPEN_PDF.key, true)) }

    Row {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("PDF Generator automatisch starten?")
                Checkbox(
                    checked = autoStart,
                    onCheckedChange = {
                        autoStart = it
                        prefs.putBoolean(Prefs.AUTO_LAUNCH.key, it)
                    },
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("PDF na genereren openen?")
                Checkbox(
                    checked = autoOpen,
                    onCheckedChange = {
                        autoOpen = it
                        prefs.putBoolean(Prefs.AUTO_OPEN_PDF.key, it)
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
