package screens.preferences

import FORMAT_LDT
import TimeProvider
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
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
import formatterLD
import formatterLDT
import getFirstOfRRule
import getWeekNumberOf
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
    timeProvider: TimeProvider,
) {
    val rruleList =
        mutableStateListOf<RRuleSetV1>().apply {
            addAll(preferencesStore.rruleSets)
        }
    var rruleErrorMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    var rruleErrorMsg by remember { mutableStateOf("") }
    var showRruleErrorPopup by remember { mutableStateOf(false) }

    var saveMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    var saveMsg by remember { mutableStateOf("") }
    var showSavePopup by remember { mutableStateOf(false) }

    var testRRuleTimer by remember { mutableStateOf<TimerTask?>(null) }
    var testRRuleMsg by remember { mutableStateOf("") }
    var showTestRRulePopup by remember { mutableStateOf(false) }

    PopupBox(popupWidth = 500F, popupHeight = 150F, showPopup = showTestRRulePopup, onClickOutside = {
        showTestRRulePopup = false
    }, content = {
        Text(
            testRRuleMsg,
            modifier =
                Modifier.semantics {
                    contentDescription =
                        "Test-RRule-popup-text"
                },
        )
    })

    PopupBox(popupWidth = 300F, popupHeight = 150F, showPopup = showSavePopup, onClickOutside = {
        showSavePopup = false
    }, content = {
        Text(
            saveMsg,
            modifier =
                Modifier.semantics {
                    contentDescription =
                        "Save-popup-text"
                },
        )
    })
    PopupBox(popupWidth = 200F, popupHeight = 150F, showPopup = showRruleErrorPopup, onClickOutside = {
        showRruleErrorPopup =
            false
    }, content = {
        Text(
            rruleErrorMsg,
            color = MaterialTheme.colors.error,
            modifier =
                Modifier.semantics {
                    contentDescription =
                        "RRule-popup-text"
                },
        )
    })

    fun resetRRuleTimer(): TimerTask =
        Timer("Reset error message").schedule(3000, 5000L) {
            showRruleErrorPopup = false
            rruleErrorMsg = ""
            rruleErrorMsgTimer?.cancel()
        }

    fun resetSaveMsgTimer(): TimerTask =
        Timer("Reset save message").schedule(3000, 5000L) {
            showSavePopup = false
            saveMsg = ""
            saveMsgTimer?.cancel()
        }

    fun setRRuleErrorMsg(msg: String) {
        rruleErrorMsgTimer?.cancel()
        showRruleErrorPopup = true
        rruleErrorMsg = msg
    }

    fun setSaveMsg(msg: String = "🗓️ Opgeslagen!") {
        saveMsgTimer?.cancel()
        saveMsg = msg
        showSavePopup = true
        saveMsgTimer = resetSaveMsgTimer()
    }

    fun setTestRRuleMsg(msg: String) {
        testRRuleTimer?.cancel()
        testRRuleMsg = msg
        showTestRRulePopup = true
    }

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
        setSaveMsg()
    }

    fun saveLDT(
        id: Long,
        ldtString: String,
    ) {
        rruleErrorMsgTimer = resetRRuleTimer()
        val rrule = rruleList.find { it.id == id } ?: return
        val copy = rrule.copy(fromLDT = ldtString)
        rruleList.remove(rrule)
        rruleList.add(copy)
        preferencesStore.rruleSets = rruleList.toSet()

        try {
            LocalDateTime.parse(ldtString, formatterLDT)
            setSaveMsg()
        } catch (e: Exception) {
            setRRuleErrorMsg("${rrule.description} heeft aandacht nodig: ${e.message}")
            return
        }

        setSaveMsg()
    }

    fun saveRRule(
        id: Long,
        rruleString: String,
    ) {
        rruleErrorMsgTimer = resetRRuleTimer()
        val rrule = rruleList.find { it.id == id } ?: return
        val copy = rrule.copy(rrule = rruleString)
        rruleList.remove(rrule)
        rruleList.add(copy)
        preferencesStore.rruleSets = rruleList.toSet()

        try {
            RecurrenceRule(rruleString)
            setSaveMsg()
        } catch (e: Exception) {
            setRRuleErrorMsg("${rrule.description} heeft aandacht nodig: ${e.message}")
            return
        }

        // Save indicator
        setSaveMsg()
    }

    fun deleteRRule(id: Long) {
        val rrule = rruleList.find { it.id == id } ?: return
        rruleList.remove(rrule)
        preferencesStore.rruleSets = rruleList.toSet()
    }

    fun testRRule(id: Long) {
        val rrule = rruleList.find { it.id == id } ?: return
        val seed = LocalDateTime.parse(rrule.fromLDT, formatterLDT)
        val rruleResult =
            try {
                getFirstOfRRule(rrule.rrule, seed, timeProvider.getLocalDateTimeNow())
            } catch (e: Exception) {
                setRRuleErrorMsg("${rrule.description} heeft aandacht nodig: ${e.message}")
                return
            }
        when (rruleResult) {
            LocalDateTimeResult.NotFound -> {
                setRRuleErrorMsg("Geen overeenkomende tijd gevonden.")
            }
            is LocalDateTimeResult.Success -> {
                setTestRRuleMsg(
                    "${rrule.description} zal eerstvolgend herhalen op:\n${rruleResult.localDateTime.format(
                        formatterLD,
                    )}\nWeek: ${getWeekNumberOf(
                        rruleResult.localDateTime,
                    )}\n\nWeek ervoor: ${rruleResult.localDateTime.minusWeeks(1).format(formatterLD)}",
                )
            }
        }
    }

    MaterialTheme {
        Row {
            Column(Modifier.width(10.dp)) {}
            Column {
                Button(onClick = { addRRule() }) {
                    Text("+ Recurrence rule")
                }
            }
        }
        Row(modifier = Modifier.height(20.dp)) {}
        Row {
            Column(Modifier.fillMaxHeight().fillMaxWidth()) {
                // Only show of the current profile
                rruleList
                    .filter { it.profileId == activeProfile.value }
                    .sortedBy { it.id }
                    .map { rruleSet ->
                        Row(Modifier.fillMaxWidth()) {
                            Column(Modifier.width(10.dp)) {}
                            Column(Modifier.width(40.dp)) {
                                Button(
                                    onClick = {
                                        println("AAH")
                                        testRRule(rruleSet.id)
                                    },
                                    modifier =
                                        Modifier.semantics {
                                            contentDescription = "RRule-${rruleSet.id}-info"
                                        },
                                ) {
                                    Text(
                                        "?",
                                    )
                                }
                            }
                            Column(Modifier.width(10.dp)) {}
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
                            Column(Modifier.width(280.dp)) {
                                TextField(
                                    label = { Text("RRule") },
                                    value = rruleSet.rrule,
                                    onValueChange = {
                                        saveRRule(rruleSet.id, it)
                                    },
                                    modifier =
                                        Modifier
                                            .semantics {
                                                contentDescription = "RRule-${rruleSet.id}-RRule"
                                            }.fillMaxWidth(),
                                )
                            }
                            Column(Modifier.width(10.dp)) {}
                            Column(Modifier.width(40.dp)) {
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
        Row {
            Column(Modifier.width(10.dp)) {}
            Column {
                Text(
                    "Een recurrence rule (RRule) beschrijft iets wat zich herhaalt in de tijd.\n" +
                        "Voorbeeld: Elke maand op vrijdag: " +
                        "RRULE:FREQ=MONTHLY;BYDAY=FR\n" +
                        "Meer zijn te vinden op: https://jkbrzt.github.io/rrule\n" +
                        "De scrollbars kreeg ik niet werkend maar scrollen werkt wel 🪜",
                )
            }
        }
    }
}
