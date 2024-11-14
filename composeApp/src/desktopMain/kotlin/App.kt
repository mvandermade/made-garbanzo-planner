import androidx.compose.runtime.*
import model.AppState
import screen.preferences
import screen.start
import java.util.prefs.Preferences

@Composable
fun App(prefs: Preferences) {
    var appState by remember { mutableStateOf(AppState.START) }

    fun requestNewAppState(requestedNewAppState: AppState) {
        appState = requestedNewAppState
    }

    when (appState) {
        AppState.START -> start(::requestNewAppState, prefs)
        AppState.PREFERENCES -> preferences(::requestNewAppState, prefs)
    }
}
