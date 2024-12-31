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
import waitUntilText
import java.util.prefs.Preferences
import kotlin.test.assertEquals

class ProfileSwitcher {
    @get:Rule
    val cr = createComposeRule()

    private lateinit var preferencesStore: PreferencesStore

    @Before
    fun setUp() {
        val preferences = Preferences.userRoot().node(PreferenceStoreMock::class.java.name)
        preferences.clear()

        val preferencesStoreInternal = PreferencesStoreInternal(preferences)
        migratePreferences(preferencesStoreInternal)
        preferencesStore = PreferencesStore(preferences, preferencesStoreInternal.readV1())
    }

    @Test
    fun `Set RRule and then generate a PDF Expect it to be on it`() {
        cr.setContent {
            App(preferencesStore)
        }

        assertEquals(1, preferencesStore.activeProfile)

        cr.waitUntilText("Sport")
        cr.onNodeWithText("Sport").performClick()

        assertEquals(3, preferencesStore.activeProfile)
    }
}
