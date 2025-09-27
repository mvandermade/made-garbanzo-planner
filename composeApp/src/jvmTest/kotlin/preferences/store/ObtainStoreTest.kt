import constants.APP_PREFERENCES_JSON
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ProfileV1
import models.RRuleSetV1
import models.preferences.PreferencesV1
import models.preferences.PreferencesV2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import preferences.store.ObtainedStoreResult
import preferences.store.obtainStore
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

        assertTrue(result is ObtainedStoreResult.Error)
    }

    @Test
    fun `Migration steps for a V0 till current version internal`() {
        val internalV0 =
            PreferencesV1(
                version = 0L,
                rruleSets = emptySet(),
                activeProfile = 0L,
                onStartUpOpenPDF = true,
                profiles = setOf(ProfileV1(0L, "default")),
                startDate = "2020-01-01",
                startDateIsEnabled = true,
                autoOpenPDFAfterGenerationIsEnabled = false,
                pdfOutputPath = "/tmp/output.pdf",
            )

        val prefs = freshPrefsNode()
        prefs.put(APP_PREFERENCES_JSON, json.encodeToString(internalV0))

        val result = obtainStore(javaPreferences = prefs)

        assertTrue(result is ObtainedStoreResult.Ready)
        val store = (result as ObtainedStoreResult.Ready).store

        assertEquals(
            setOf(
                RRuleSetV1(
                    profileId = 1,
                    id = 1,
                    description = "Dagelijks herhalen",
                    rrule = "FREQ=DAILY",
                    fromLDT = "27-09-2025 10:30",
                ),
            ),
            store.rruleSets,
        )
        assertEquals(1, store.activeProfile)
        assertEquals(true, store.onStartUpOpenPDF)
        assertEquals(
            setOf(
                ProfileV1(id = 1, name = "Thuis"),
                ProfileV1(id = 2, name = "Werk"),
                ProfileV1(id = 3, name = "Sport"),
            ),
            store.profiles,
        )
        assertEquals("01-01-2000", store.startDate)
        assertEquals(false, store.startDateIsEnabled)
        assertEquals(true, store.autoOpenPDFAfterGenerationIsEnabled)
        assertEquals("", store.pdfOutputPath)
        assertEquals(false, store.externalPreferencesIsEnabled)
        assertEquals("", store.externalPreferencesPath)
    }

    @Test
    fun `Migration steps for a V0 till current version external`() {
        fun sourceV4(
            externalEnabled: Boolean = false,
            externalPath: String = "",
            onStartUpOpenPDF: Boolean = false,
            startDateEnabled: Boolean = true,
            pdfOutputPath: String = "",
        ) = PreferencesV2(
            version = 4L,
            rruleSets =
                setOf(
                    RRuleSetV1(
                        profileId = 1,
                        id = 99,
                        description = "Take a walk",
                        rrule = "FREQ=DAILY",
                        fromLDT = "27-09-2025 22:30",
                    ),
                ),
            activeProfile = 0L,
            onStartUpOpenPDF = onStartUpOpenPDF,
            profiles = setOf(ProfileV1(0L, "default")),
            startDate = "01-01-2020",
            startDateIsEnabled = startDateEnabled,
            autoOpenPDFAfterGenerationIsEnabled = false,
            pdfOutputPath = pdfOutputPath,
            externalPreferencesIsEnabled = externalEnabled,
            externalPreferencesPath = externalPath,
        )

        val externalFile = File.createTempFile("prefs-external", ".json")
        externalFile.deleteOnExit()

        // Prepare external preferences content
        val externalV0 =
            sourceV4(
                externalEnabled = false,
                onStartUpOpenPDF = true,
                startDateEnabled = false,
            )
        externalFile.writeText(json.encodeToString(externalV0))

        // Internal preferences point to external and want to open PDF
        val internalV0 =
            sourceV4(
                externalEnabled = true,
                externalPath = externalFile.absolutePath,
                onStartUpOpenPDF = false,
                startDateEnabled = true,
            )

        // Set the internal store to call the external migration
        val prefs = freshPrefsNode()
        prefs.put(APP_PREFERENCES_JSON, json.encodeToString(internalV0))

        val result = obtainStore(javaPreferences = prefs)

        assertTrue(result is ObtainedStoreResult.Ready)
        val store = (result as ObtainedStoreResult.Ready).store

        assertEquals(
            setOf(
                RRuleSetV1(
                    profileId = 1,
                    id = 99,
                    description = "Take a walk",
                    rrule = "FREQ=DAILY",
                    fromLDT = "27-09-2025 22:30",
                ),
            ),
            store.rruleSets,
        )
        assertEquals(0, store.activeProfile)
        assertEquals(true, store.onStartUpOpenPDF)
        assertEquals(setOf(ProfileV1(0L, "default")), store.profiles)
        assertEquals("01-01-2020", store.startDate)
        assertEquals(false, store.startDateIsEnabled)
        assertEquals(false, store.autoOpenPDFAfterGenerationIsEnabled)
        assertEquals("", store.pdfOutputPath)
        assertEquals(true, store.externalPreferencesIsEnabled)
        assertEquals(externalFile.absolutePath, store.externalPreferencesPath)
    }
}
