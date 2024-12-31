package screens.preferences

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import formatterLDT
import models.RRuleSetV1
import org.dmfs.rfc5545.recur.RecurrenceRule
import repositories.PreferencesStore
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.schedule

@Composable
fun RRuleSetRows(
    preferencesStore: PreferencesStore,
    activeProfile: MutableState<Long>,
) {
    var rruleErrorMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    var saveMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    val rruleList =
        mutableStateListOf<RRuleSetV1>().apply {
            addAll(preferencesStore.rruleSets)
        }
    var rruleErrorMsg by remember { mutableStateOf("") }
    var saveMsg by remember { mutableStateOf("") }

    fun addRRule() {
        val element = rruleList.maxByOrNull { it.id }
        val newId =
            if (element == null) {
                1L
            } else {
                element.id + 1
            }
        rruleList +=
            RRuleSetV1(
                activeProfile.value,
                newId,
                "",
                "",
                LocalDateTime.now().format(formatterLDT),
            )
        // ToSet to prevent duplicates
        preferencesStore.rruleSets = rruleList.toSet()
    }

    fun saveRRuleDescription(
        id: Long,
        description: String,
    ) {
        val rrule = rruleList.find { it.id == id } ?: return
        val copy = rrule.copy(description = description)
        rruleList.remove(rrule)
        rruleList.add(copy)
        preferencesStore.rruleSets = rruleList.toSet()
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
        val rrule = rruleList.find { it.id == id } ?: return
        val copy = rrule.copy(fromLDT = ldtString)
        rruleList.remove(rrule)
        rruleList.add(copy)
        preferencesStore.rruleSets = rruleList.toSet()

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
        val rrule = rruleList.find { it.id == id } ?: return
        val copy = rrule.copy(rrule = rruleString)
        rruleList.remove(rrule)
        rruleList.add(copy)
        preferencesStore.rruleSets = rruleList.toSet()

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
        val rrule = rruleList.find { it.id == id } ?: return
        rruleList.remove(rrule)
        preferencesStore.rruleSets = rruleList.toSet()
    }

    MaterialTheme {
        Row {
            Column {
                Button(onClick = { addRRule() }) {
                    Text("+ Recurrence rule")
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
            Column {
                Text(
                    "Een recurrence rule (RRULE) beschrijft iets wat zich herhaalt in de tijd." +
                        " Voorbeelden zijn te vinden op: https://jkbrzt.github.io/rrule",
                )
            }
        }
        Row {
            Column(Modifier.fillMaxHeight().fillMaxWidth()) {
                // Only show of the current profile
                rruleList
                    .filter { it.profileId == activeProfile.value }
                    .sortedBy { it.id }.map { rruleSet ->
                        Row(Modifier.fillMaxWidth()) {
                            Column(Modifier.width(200.dp)) {
                                TextField(
                                    label = { Text("Beschrijving") },
                                    value = rruleSet.description,
                                    onValueChange = {
                                        saveRRuleDescription(rruleSet.id, it)
                                    },
                                    modifier =
                                        Modifier.semantics {
                                            contentDescription = "RRule-${rruleSet.id}-description"
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
                                    modifier =
                                        Modifier.semantics {
                                            contentDescription = "RRule-${rruleSet.id}-from"
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
                                    modifier =
                                        Modifier.semantics {
                                            contentDescription = "RRule-${rruleSet.id}-RRule"
                                        },
                                )
                            }
                            Column(Modifier.width(100.dp)) {
                                Button(onClick = { deleteRRule(rruleSet.id) }) {
                                    Text(
                                        "X",
                                        modifier =
                                            Modifier.semantics {
                                                contentDescription = "RRule-${rruleSet.id}-delete"
                                            },
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}
