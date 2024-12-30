import androidx.compose.runtime.*
import models.AppState
import repositories.PreferencesStore
import screens.preferences
import screens.start

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
