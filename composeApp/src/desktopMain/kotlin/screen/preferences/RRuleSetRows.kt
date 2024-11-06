package screen.preferences

import FORMAT_LDT
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import formatterLDT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Prefs
import model.RRuleSet
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.time.LocalDateTime
import java.util.*
import java.util.prefs.Preferences
import kotlin.concurrent.schedule

@Composable
fun RRuleSetRows(
    prefs: Preferences,
    activeProfile: MutableState<String>,
) {
    var rruleErrorMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    var saveMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    var rruleSetsString by remember { mutableStateOf(prefs.get(Prefs.RRULE_SETS.key, "[]")) }
    var rruleErrorMsg by remember { mutableStateOf("") }
    var saveMsg by remember { mutableStateOf("") }

    // Enforce the equals method
    val rruleSetsSet =
        try {
            Json.decodeFromString<List<RRuleSet>>(rruleSetsString).toMutableSet()
        } catch (e: Exception) {
            mutableSetOf()
        }

    fun updateRRuleStateFromSet() {
        val content = Json.encodeToString(rruleSetsSet)
        prefs.put(Prefs.RRULE_SETS.key, content)
        rruleSetsString = content
    }

    fun addRRule() {
        val element = rruleSetsSet.maxByOrNull { it.id }
        val newId =
            if (element == null) {
                1
            } else {
                element.id + 1
            }
        rruleSetsSet += RRuleSet(activeProfile.value.toLong(), newId, "", "", LocalDateTime.now().format(formatterLDT))
        updateRRuleStateFromSet()
    }

    fun saveRRuleDescription(
        id: Long,
        description: String,
    ) {
        val rrule = rruleSetsSet.find { it.id == id } ?: return
        val copy = rrule.copy(description = description)
        rruleSetsSet.remove(rrule)
        rruleSetsSet.add(copy)
        updateRRuleStateFromSet()
        saveMsgTimer?.cancel()
        saveMsg = "💬Opgeslagen!"
        saveMsgTimer =
            Timer("Reset save message").schedule(3000, 5000L) {
                saveMsg = ""
                saveMsgTimer?.cancel()
            }
    }

    fun saveLDT(
        id: Long,
        ldtString: String,
    ) {
        val rrule = rruleSetsSet.find { it.id == id } ?: return
        val copy = rrule.copy(fromLDT = ldtString)
        rruleSetsSet.remove(rrule)
        rruleSetsSet.add(copy)
        updateRRuleStateFromSet()

        try {
            LocalDateTime.parse(ldtString, formatterLDT)
            rruleErrorMsg = "✅"
            rruleErrorMsgTimer =
                Timer("Reset error message").schedule(3000, 5000L) {
                    rruleErrorMsg = ""
                    rruleErrorMsgTimer?.cancel()
                }
        } catch (e: Exception) {
            rruleErrorMsgTimer?.cancel()
            rruleErrorMsg = "${rrule.description} heeft aandacht nodig: ${e.message}"
            return
        }

        saveMsgTimer?.cancel()
        saveMsg = "💬Opgeslagen!"
        saveMsgTimer =
            Timer("Reset save message").schedule(3000, 5000L) {
                saveMsg = ""
                saveMsgTimer?.cancel()
            }
    }

    fun saveRRule(
        id: Long,
        rruleString: String,
    ) {
        // Verify
        val rrule = rruleSetsSet.find { it.id == id } ?: return
        val copy = rrule.copy(rrule = rruleString)
        rruleSetsSet.remove(rrule)
        rruleSetsSet.add(copy)
        updateRRuleStateFromSet()

        try {
            RecurrenceRule(rruleString)
            rruleErrorMsg = "✅"
            rruleErrorMsgTimer =
                Timer("Reset error message").schedule(3000, 5000L) {
                    rruleErrorMsg = ""
                    rruleErrorMsgTimer?.cancel()
                }
        } catch (e: Exception) {
            rruleErrorMsgTimer?.cancel()
            rruleErrorMsg = "${rrule.description} heeft aandacht nodig: ${e.message}"
            return
        }

        // Save indicator
        saveMsg = "🗓️Opgeslagen!"
        saveMsgTimer?.cancel()
        saveMsgTimer =
            Timer("Reset save message").schedule(3000, 5000L) {
                saveMsg = ""
                saveMsgTimer?.cancel()
            }
    }

    fun deleteRRule(id: Long) {
        val rrule = rruleSetsSet.find { it.id == id } ?: return
        rruleSetsSet.remove(rrule)
        updateRRuleStateFromSet()
    }

    MaterialTheme {
        Row {
            Column {
                Button(onClick = { addRRule() }) {
                    Text("+ RRule")
                }
            }
            Column {
                if (saveMsg != "") {
                    Text(saveMsg)
                }
            }
            Column {
                if (rruleErrorMsg != "") {
                    Text(rruleErrorMsg, color = MaterialTheme.colors.error)
                }
            }
        }
        Row {
            Column(Modifier.fillMaxHeight().fillMaxWidth()) {
                // Only show of the current profile
                rruleSetsSet
                    .filter { it.profileId == activeProfile.value.toLong() }
                    .sortedBy { it.id }.map { rruleSet ->
                        Row(Modifier.fillMaxWidth()) {
                            Column(Modifier.width(200.dp)) {
                                TextField(
                                    label = { Text("Beschrijving") },
                                    value = rruleSet.description,
                                    onValueChange = {
                                        saveRRuleDescription(rruleSet.id, it)
                                    },
                                )
                            }
                            Column(Modifier.width(200.dp)) {
                                TextField(
                                    label = { Text("Vanaf: $FORMAT_LDT") },
                                    value = rruleSet.fromLDT,
                                    onValueChange = {
                                        saveLDT(rruleSet.id, it)
                                    },
                                )
                            }
                            Column(Modifier.width(300.dp)) {
                                TextField(
                                    label = { Text("RRule") },
                                    value = rruleSet.rrule,
                                    onValueChange = {
                                        saveRRule(rruleSet.id, it)
                                    },
                                )
                            }
                            Column(Modifier.width(100.dp)) {
                                Button(onClick = { deleteRRule(rruleSet.id) }) {
                                    Text("X")
                                }
                            }
                        }
                    }
            }
        }
    }
}
