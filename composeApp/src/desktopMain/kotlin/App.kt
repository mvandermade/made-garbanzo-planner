import androidx.compose.runtime.*
import model.AppState
import repositories.PreferencesStore
import screen.preferences
import screen.start

@Composable
fun App(preferencesStore: PreferencesStore) {
    var appState by remember { mutableStateOf(AppState.START) }

    fun requestNewAppState(requestedNewAppState: AppState) {
        appState = requestedNewAppState
    }

    when (appState) {
        AppState.START -> start(::requestNewAppState, preferencesStore)
        AppState.PREFERENCES -> preferences(::requestNewAppState, preferencesStore)
    }
}
