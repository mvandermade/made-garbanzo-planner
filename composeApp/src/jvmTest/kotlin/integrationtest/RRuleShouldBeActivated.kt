package integrationtest

import App
import TimeProvider
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import integrationtest.preferences.PreferenceStoreMock
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import preferences.PreferencesStoreInternal
import preferences.store.migratePreferences
import repositories.PreferencesStore
import waitUntilText
import java.io.File
import java.util.prefs.Preferences
import kotlin.test.assertContains

class RRuleShouldBeActivated {
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
    fun `Set RRule and then generate a PDF Expect it to be on it`() {
        cr.setContent {
            App(preferencesStore, TimeProvider())
        }

        cr.waitUntilText("Ga naar instellingen ⚙️")
        cr.onNodeWithText("Ga naar instellingen ⚙️").performClick()

        cr.waitUntilText("Ga terug naar start")

        // Disable auto popup of the PDF
        cr.onNodeWithText("Geavanceerde instellingen").performClick()
        cr.waitUntilText("Ga terug naar instellingen")
        cr.onNodeWithContentDescription("auto-open-pdf").performClick()
        cr.onNodeWithText("Ga terug naar instellingen").performClick()

        cr.onNodeWithText("+ Recurrence rule").performClick()

        cr.onNodeWithContentDescription("RRule-2-description").performTextInput("TESTRRULE")
        cr.onNodeWithContentDescription("RRule-2-RRule").performTextInput("FREQ=DAILY")

        // Go back to menu and get PDF
        cr.onNodeWithText("Ga terug naar start").performClick()
        cr.waitUntilText("Genereer PDF 📜")
        cr.onNodeWithText("Genereer PDF 📜").performClick()

        // Way of getting hold of the PDF is a text field hidden for most users
        val node = cr.onNodeWithContentDescription("pdfPath").fetchSemanticsNode()
        val pdfPath = node.config.getOrNull(SemanticsProperties.EditableText)?.text

        val doc = Loader.loadPDF(File(pdfPath!!))

        val text = PDFTextStripper().getText(doc)

        assertContains(text, "TESTRRULE")
    }

    @Test
    fun `Set RRule with bad syntax expect message on the screen`() {
        cr.setContent {
            App(preferencesStore, TimeProvider())
        }

        cr.waitUntilText("Ga naar instellingen ⚙️")
        cr.onNodeWithText("Ga naar instellingen ⚙️").performClick()

        cr.waitUntilText("Ga terug naar start")

        cr.onNodeWithText("+ Recurrence rule").performClick()

        cr.onNodeWithContentDescription("RRule-2-description").performTextInput("TESTRRULE")
        cr.onNodeWithContentDescription("RRule-2-RRule").performTextInput("FREQ=DAIL")

        cr
            .onNodeWithContentDescription(
                "RRule-popup-text",
            ).assertTextContains("TESTRRULE heeft aandacht nodig: FREQ part is missing")
    }

    // Could not finish this test because the performClick does not seem to fire on the onclick {} handler
    // I tried to println() debug but this did not show up in the console.
//    @Test
//    fun `Clicking the help button will display helpful text that is correct`() {
//        val timeProvider = mockk<TimeProvider>()
//        cr.setContent {
//            App(preferencesStore, timeProvider)
//        }
//
//        cr.waitUntilText("Ga naar instellingen ⚙️")
//        cr.onNodeWithText("Ga naar instellingen ⚙️").performClick()
//
//        cr.waitUntilText("Ga terug naar start")
//
//        cr.onNodeWithText("+ Recurrence rule").performClick()
//
//        cr.onNodeWithContentDescription("RRule-2-description").performTextInput("TESTRRULE")
//        cr.onNodeWithContentDescription("RRule-2-RRule").performTextInput("FREQ=MONTHLY")
//
//        cr.onNodeWithContentDescription("RRule-2-info").performClick()
//
//        cr.waitUntilText("Data blabla")
//
//        cr
//            .onNodeWithContentDescription(
//                "Test-RRule-popup-text",
//            ).assertTextContains("Data blabla")
//    }
}
