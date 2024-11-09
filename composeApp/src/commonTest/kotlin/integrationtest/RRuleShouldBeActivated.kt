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
import org.junit.Rule
import org.junit.Test
import waitUntilText
import java.io.File
import java.util.prefs.Preferences
import kotlin.test.assertContains

class RRuleShouldBeActivated {
    @get:Rule
    val cr = createComposeRule()

    @Test
    fun `Set RRule and then generate a PDF Expect it to be on it`() {
        val prefs = Preferences.userRoot().node(PreferenceStoreMock::class.java.name)
        // For testing this is required
        prefs.clear()

        cr.setContent {
            App(prefs)
        }

        cr.waitUntilText("Ga naar instellingen ⚙️")
        cr.onNodeWithText("Ga naar instellingen ⚙️").performClick()

        cr.waitUntilText("Ga terug naar start")

        // Disable auto popup of the PDF
        cr.onNodeWithContentDescription("auto-open-pdf").performClick()

        cr.onNodeWithText("+ RRule").performClick()

        cr.onNodeWithContentDescription("RRule-1-description").performTextInput("TESTRRULE")
        cr.onNodeWithContentDescription("RRule-1-RRule").performTextInput("FREQ=DAILY")

        // Go back to menu and get PDF
        cr.onNodeWithText("Ga terug naar start").performClick()
        cr.waitUntilText("Genereer PDF 📜(of druk op ENTER)")
        cr.onNodeWithText("Genereer PDF 📜(of druk op ENTER)").performClick()

        // Way of getting hold of the PDF is a text field hidden for most users
        val node = cr.onNodeWithContentDescription("pdfPath").fetchSemanticsNode()
        val pdfPath = node.config.getOrNull(SemanticsProperties.EditableText)?.text

        val doc = Loader.loadPDF(File(pdfPath!!))

        val text = PDFTextStripper().getText(doc)

        assertContains(text, "TESTRRULE")
    }
}
