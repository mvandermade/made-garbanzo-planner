package screens.start

import FORMAT_LD
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import formatterLD
import getWeekNumberOfNextMonday
import repositories.PreferencesStore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.schedule

@Composable
fun DatePickerRow(preferencesStore: PreferencesStore) {
    var startDateEnabled by remember { mutableStateOf(preferencesStore.startDateIsEnabled) }
    var localDateString by remember { mutableStateOf(preferencesStore.startDate) }

    var infoMsg by remember { mutableStateOf("") }
    var infoMsgTimer by remember { mutableStateOf<TimerTask?>(null) }

    fun saveLD(value: String) {
        localDateString = value
        preferencesStore.startDate = value
        try {
            val localDate = LocalDate.parse(value, formatterLD)
            val fromLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
            infoMsg = "✅ genereert week: ${getWeekNumberOfNextMonday(fromLocalDateTime)}"
            infoMsgTimer =
                Timer("Reset infoMsg").schedule(3000, 5000L) {
                    infoMsg = ""
                    infoMsgTimer?.cancel()
                }
        } catch (e: Exception) {
            infoMsgTimer?.cancel()
            infoMsg = "Heeft aandacht nodig: ${e.message}"
        }
    }

    Row {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Gebruik (als test) een andere start datum dan maandag a.s.")
            Checkbox(
                checked = startDateEnabled,
                onCheckedChange = {
                    preferencesStore.startDateIsEnabled = it
                    startDateEnabled = it
                },
            )
        }
    }
    if (startDateEnabled) {
        Row {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    label = { Text("Vanaf eerste maandag na: $FORMAT_LD") },
                    value = localDateString,
                    onValueChange = {
                        saveLD(it)
                    },
                )
            }
        }
        if (infoMsg.isNotEmpty()) {
            Row {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(infoMsg)
                }
            }
        }
    }
}
