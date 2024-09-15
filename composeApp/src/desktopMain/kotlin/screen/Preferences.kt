package screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.AppState
import model.Profile
import model.RRuleSet
import net.fortuna.ical4j.model.Recur
import java.time.LocalDateTime
import java.util.*
import java.util.prefs.Preferences
import kotlin.concurrent.schedule

@Composable
fun preferences(
    requestNewAppState: (appState: AppState) -> Unit,
    prefs: Preferences,
) {
    // To let the user see the change immediately
    var autoStart by remember { mutableStateOf(prefs.getBoolean("app.auto-launch", false)) }
    var activeProfile by remember { mutableStateOf(prefs.get("app.active-profile", "0")) }
    val profilesString by remember {
        mutableStateOf(
            prefs.get(
                "app.profiles",
                """
                [{"id":1, "name":"Profile1"}, {"id":2, "name":"Profile2"}]
                """.trimIndent(),
            ),
        )
    }
    var rruleSetsString by remember { mutableStateOf(prefs.get("app.rruleSets", "[]")) }
    var rruleErrorMsg by remember { mutableStateOf("") }
    var saveMsg by remember { mutableStateOf("") }
    var rruleErrorMsgTimer by remember { mutableStateOf<TimerTask?>(null) }
    var saveMsgTimer by remember { mutableStateOf<TimerTask?>(null) }

    // Enforce the equals method
    val profilesSet =
        try {
            Json.decodeFromString<List<Profile>>(profilesString).toSet()
        } catch (e: Exception) {
            mutableSetOf()
        }
    // Enforce the equals method
    val rruleSetsSet =
        try {
            Json.decodeFromString<List<RRuleSet>>(rruleSetsString).toMutableSet()
        } catch (e: Exception) {
            mutableSetOf()
        }

    fun updateRRuleStateFromSet() {
        val content = Json.encodeToString(rruleSetsSet)
        prefs.put("app.rruleSets", content)
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
        rruleSetsSet += RRuleSet(activeProfile.toLong(), newId, "", "")
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
        saveMsg = "💬🫙"
        saveMsgTimer =
            Timer("Reset save message").schedule(3000, 5000L) {
                saveMsg = ""
                saveMsgTimer?.cancel()
            }
    }

    fun saveRRule(
        id: Long,
        rRule: String,
    ) {
        // Verify
        val rrule = rruleSetsSet.find { it.id == id } ?: return
        val copy = rrule.copy(RRule = rRule)
        rruleSetsSet.remove(rrule)
        rruleSetsSet.add(copy)
        updateRRuleStateFromSet()

        try {
            Recur<LocalDateTime>(rRule)
            rruleErrorMsg = "✅"
            rruleErrorMsgTimer =
                Timer("Reset error message").schedule(3000, 5000L) {
                    rruleErrorMsg = ""
                    rruleErrorMsgTimer?.cancel()
                }
        } catch (e: Exception) {
            rruleErrorMsgTimer?.cancel()
            rruleErrorMsg = "${rrule.description} heeft een foutje: ${e.message}"
            return
        }

        // Save indicator
        saveMsg = "🗓️🫙"
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
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(Modifier.fillMaxWidth(0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    profilesSet.map { profile ->
                        RadioButton(selected = activeProfile.toLong() == profile.id, onClick = {
                            activeProfile = profile.id.toString()
                            prefs.put("app.active-profile", activeProfile)
                        })
                        ClickableText(AnnotatedString(profile.name), onClick = {
                            activeProfile = profile.id.toString()
                            prefs.put("app.active-profile", activeProfile)
                        })
                    }
                }
            }
            Column {
                Row {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { requestNewAppState(AppState.START) }) {
                            Text("Ga terug naar start")
                        }
                    }
                }
                Row {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("PDF Generator automatisch starten")
                            Checkbox(
                                checked = autoStart,
                                onCheckedChange = {
                                    autoStart = it
                                    prefs.putBoolean("app.auto-launch", it)
                                },
                            )
                        }
                        Row {
                            Row {
                                if (rruleErrorMsg != "") {
                                    Text(rruleErrorMsg, color = MaterialTheme.colors.error)
                                }
                            }
                        }
                        Row {
                            Column {
                                if (saveMsg != "") {
                                    Text(saveMsg)
                                }
                            }
                            Column {
                                Button(onClick = { addRRule() }) {
                                    Text("+ RRule")
                                }
                            }
                        }
                    }
                }
                Row {
                    Column(Modifier.fillMaxHeight()) {
                        // Only show of the current profile
                        rruleSetsSet
                            .filter { it.profileId == activeProfile.toLong() }
                            .sortedBy { it.id }.map { rruleSet ->
                                Row {
                                    Column {
                                        TextField(
                                            label = { Text("Beschrijving") },
                                            value = rruleSet.description,
                                            onValueChange = {
                                                saveRRuleDescription(rruleSet.id, it)
                                            },
                                        )
                                    }
                                    Column {
                                        TextField(
                                            label = { Text("RRule") },
                                            value = rruleSet.RRule,
                                            onValueChange = {
                                                saveRRule(rruleSet.id, it)
                                            },
                                        )
                                    }
                                    Column {
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
    }
}
