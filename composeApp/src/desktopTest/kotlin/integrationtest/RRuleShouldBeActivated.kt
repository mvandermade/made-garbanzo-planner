package integrationtest

import App
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
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
import preferences.migratePreferences
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
        preferencesStore = PreferencesStore(preferences, preferencesStoreInternal.readV1())
    }

    @Test
    fun `Set RRule and then generate a PDF Expect it to be on it`() {
        cr.setContent {
            App(preferencesStore)
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

        cr.onNodeWithContentDescription("RRule-1-description").performTextInput("TESTRRULE")
        cr.onNodeWithContentDescription("RRule-1-RRule").performTextInput("FREQ=DAILY")

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
}
