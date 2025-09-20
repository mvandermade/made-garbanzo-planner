import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ProfileV1
import models.preferences.PreferencesV2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import java.util.prefs.Preferences
import kotlin.test.Test

class ObtainStoreTest {
    private val json = Json

    private fun freshPrefsNode(): Preferences =
        Preferences.userRoot().node("made-garbanzo-planner-test-" + System.nanoTime())

    @Test
    fun `Invalid json returns error`() {
        val prefs = freshPrefsNode()
        // Write invalid JSON so migration/read will fail
        prefs.put(APP_PREFERENCES_JSON, "{ invalid json")

        val result = obtainStore(javaPreferences = prefs)

        assertTrue(result is StartupResult.PreferenceError)
    }

    private fun sampleMigrationV4(
        externalEnabled: Boolean = false,
        externalPath: String = "",
        onStartUpOpenPDF: Boolean = false,
        startDateEnabled: Boolean = true,
        pdfOutputPath: String = "",
    ): PreferencesV2 =
        PreferencesV2(
            version = 4L,
            rruleSets = emptySet(),
            activeProfile = 0L,
            onStartUpOpenPDF = onStartUpOpenPDF,
            profiles = setOf(ProfileV1(0L, "default")),
            startDate = "2020-01-01",
            startDateIsEnabled = startDateEnabled,
            autoOpenPDFAfterGenerationIsEnabled = false,
            pdfOutputPath = pdfOutputPath,
            externalPreferencesIsEnabled = externalEnabled,
            externalPreferencesPath = externalPath,
        )

    @Test
    fun `Migration steps for a V4 till current version`() {
        val prefs = freshPrefsNode()
        val externalFile = File.createTempFile("prefs-external", ".json")
        externalFile.deleteOnExit()

        // Prepare external preferences content
        val externalV2 =
            sampleMigrationV4(
                externalEnabled = false,
                onStartUpOpenPDF = true,
                startDateEnabled = true,
            )
        externalFile.writeText(json.encodeToString(externalV2))

        // Internal preferences point to external and want to open PDF
        val internalV2 =
            sampleMigrationV4(
                externalEnabled = true,
                externalPath = externalFile.absolutePath,
                onStartUpOpenPDF = false,
                startDateEnabled = true,
            )
        prefs.put(APP_PREFERENCES_JSON, json.encodeToString(internalV2))

        val result = obtainStore(javaPreferences = prefs)

        assertTrue(result is StartupResult.AppReady)
        val store = (result as StartupResult.AppReady).store

        assertTrue(store.externalPreferencesIsEnabled)
        assertEquals(externalFile.absolutePath, store.externalPreferencesPath)

        // Verify that external preferences are used
        assertEquals(false, store.startDateIsEnabled)
        assertTrue(store.pdfOutputPath.endsWith(".pdf"))
    }
}
