package integrationtest

import App
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import integrationtest.preferences.PreferenceStoreMock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import preferences.PreferencesStoreInternal
import preferences.migratePreferences
import repositories.PreferencesStore
import waitUntilSubstringText
import waitUntilText
import java.util.prefs.Preferences

class StartTest {
    @get:Rule
    val cr = createComposeRule()

    private lateinit var preferencesStore: PreferencesStore

    @Before
    fun setUp() {
        val preferences = Preferences.userRoot().node(PreferenceStoreMock::class.java.name)
        preferences.clear()

        val preferencesStoreInternal = PreferencesStoreInternal(preferences)
        migratePreferences(preferencesStoreInternal)
        preferencesStore = PreferencesStore(preferences, preferencesStoreInternal.readV2())
    }

    @Test
    fun `License should be clickable and show my name`() {
        cr.setContent {
            App(preferencesStore)
        }

        cr.waitUntilText("Licentie")
        cr.onNodeWithText("Licentie").performClick()

        cr.waitUntilSubstringText("Martijn van der Made")
    }
}
