package screen.start

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
import formatLD
import formatterLD
import getRRuleDates
import model.Prefs
import pickNextMonday
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.prefs.Preferences
import kotlin.concurrent.schedule

@Composable
fun DatePickerRow(prefs: Preferences) {
    var startDateEnabled by remember { mutableStateOf(prefs.getBoolean(Prefs.START_DATE_ENABLED.key, false)) }
    var localDateString by remember { mutableStateOf(prefs.get(Prefs.START_DATE.key, "01-01-2000")) }

    var errorMsg by remember { mutableStateOf<String>("") }
    var errorMsgTimer by remember { mutableStateOf<TimerTask?>(null) }

    fun saveLD(value: String) {
        localDateString = value
        prefs.put(Prefs.START_DATE.key, value)
        try {
            val localDate = LocalDate.parse(value, formatterLD)
            val fromLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
            val fromNextMonday = pickNextMonday(fromLocalDateTime)
            val weekNumber = fromNextMonday.get(ChronoField.ALIGNED_WEEK_OF_YEAR)
            errorMsg = "✅ week: $weekNumber"
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
                    // Because of a bug weird nullpointer first generate some dates here to initialise it (hypothesis)...
                    val dates =
                        getRRuleDates(
                            "FREQ=DAILY",
                            LocalDateTime.now().minusWeeks(2),
                            LocalDateTime.now(),
                            LocalDateTime.now().plusWeeks(1),
                        )
                    println("DATES: $dates")
                    startDateEnabled = it
                    prefs.putBoolean(Prefs.START_DATE_ENABLED.key, it)
                },
            )
        }
    }
    if (startDateEnabled) {
        Row {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    label = { Text("Vanaf: $formatLD") },
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
