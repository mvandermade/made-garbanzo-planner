package screens.start

import FORMAT_LD
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
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
import androidx.compose.ui.unit.dp
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

    var errorMsg by remember { mutableStateOf("") }
    var errorMsgTimer by remember { mutableStateOf<TimerTask?>(null) }

    fun saveLD(value: String) {
        localDateString = value
        preferencesStore.startDate = value
        try {
            val localDate = LocalDate.parse(value, formatterLD)
            val fromLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
            errorMsg = "✅ week: ${getWeekNumberOfNextMonday(fromLocalDateTime)}"
            errorMsgTimer =
                Timer("Reset error message").schedule(3000, 5000L) {
                    errorMsg = ""
                    errorMsgTimer?.cancel()
                }
        } catch (e: Exception) {
            errorMsgTimer?.cancel()
            errorMsg = "Heeft aandacht nodig: ${e.message}"
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
            if (errorMsg.isNotEmpty()) {
                Column(Modifier.width(400.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMsg, color = MaterialTheme.colors.error)
                }
            }
        }
    }
}
