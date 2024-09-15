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
import model.Prefs
import java.util.prefs.Preferences

@Composable
fun AutoLaunchRow(prefs: Preferences) {
    var autoStart by remember { mutableStateOf(prefs.getBoolean(Prefs.AUTO_LAUNCH.key, false)) }

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
        }
    }
}
