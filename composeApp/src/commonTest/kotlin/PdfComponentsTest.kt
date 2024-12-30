import io.mockk.mockk
import models.RRuleSetV1
import models.preferences.PreferencesV1
import nl.wykorijnsburger.kminrandom.minRandom
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.experimental.runners.Enclosed
import org.junit.jupiter.api.Nested
import org.junit.runner.RunWith
import repositories.PreferencesStore
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.math.floor
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Enclosed::class)
class PdfComponentsTest {
    // Ignore this inner class error, the suggestion doesn't work...
    @Nested
    class WriteSuggestions {
        private val startLocalDateTime: LocalDateTime = LocalDateTime.of(2021, 1, 1, 1, 0, 0)
        private val endLocalDateTime: LocalDateTime = startLocalDateTime.plusWeeks(1).minusNanos(1)
        private val entryLocalDateTimeString: String = LocalDateTime.of(2021, 1, 6, 1, 0, 0).format(formatterLDT)

        @Test
        fun `Daily rrule should be picked up`() {
            val rruleDescription = "PrintMe"
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = rruleDescription,
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("1/1 » $rruleDescription «", text.trimEnd())
        }

        @Test
        fun `Daily rrule should be picked up and box should match`() {
            val rruleDescription = "PrintMe"
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = rruleDescription,
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val box = writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("1/1 » $rruleDescription «", text.trimEnd())
            assertEquals(0f, box.topLeftX)
            assertEquals(-19f, floor(box.topLeftY))
            assertEquals(108f, floor(box.bottomRightX))
            assertEquals(-27f, floor(box.bottomRightY))
        }

        @Test
        fun `Daily rrule should be picked up but except too many loops`() {
            // Causes 1000 or more iterations
            val startLocalDateTimeTooLarge: LocalDateTime = LocalDateTime.of(2025, 1, 1, 1, 0, 0)
            val endLocalDateTimeTooLarge: LocalDateTime = startLocalDateTimeTooLarge.plusWeeks(1).minusNanos(1)

            val rruleDescription = "PrintMe"
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = rruleDescription,
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            writeSuggestions(
                doc,
                page,
                0f,
                0f,
                startLocalDateTimeTooLarge,
                endLocalDateTimeTooLarge,
                preferenceStore,
                true,
            )

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals(
                "0/1 » !error PrintMe Too many loops, compact the recurrence rule start date «",
                text.trimEnd(),
            )
        }

        @Test
        fun `Three daily rrule should be picked up`() {
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                            RRuleSetV1(
                                profileId = 1, id = 44, description = "PrintMe2",
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                            RRuleSetV1(
                                profileId = 1, id = 60, description = "PrintMe3",
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("3/3 » PrintMe, PrintMe2, PrintMe3 «", text.trimEnd())
        }

        @Test
        fun `No hit in current timeframe`() {
            val entryLocalDateTimeString: String =
                LocalDateTime.of(
                    2021,
                    1,
                    6,
                    1,
                    0,
                    0,
                ).plusWeeks(1).format(formatterLDT)
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "FREQ=MONTHLY", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("0/1", text.trimEnd())
        }

        @Test
        fun `Ignore faulty RRULE`() {
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                            RRuleSetV1(
                                profileId = 1, id = 3, description = "NO_PRINT",
                                rrule = "OOPS", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("1/2 » PrintMe, !error NO_PRINT FREQ part is missing «", text.trimEnd())
        }

        @Test
        fun `Ignore RRULE of other profile`() {
            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                            RRuleSetV1(
                                profileId = 2, id = 3, description = "NO_PRINT",
                                rrule = "FREQ=DAILY", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("1/1 » PrintMe «", text.trimEnd())
        }

        @Test
        fun `Interval rule is processed correctly expect it at the beginning of period`() {
            val entryLocalDateTimeString = LocalDateTime.of(2024, 6, 15, 1, 0, 0).format(formatterLDT)

            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "INTERVAL=3;FREQ=MONTHLY;BYDAY=3SA", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val startLocalDateTime = LocalDateTime.of(2024, 6, 9, 1, 0, 0)
            val endLocalDateTime = startLocalDateTime.plusWeeks(1).minusNanos(1)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("1/1 » PrintMe «", text.trimEnd())
        }

        @Test
        fun `Interval rule is processed correctly expect it at the second pass of period`() {
            val entryLocalDateTimeString = LocalDateTime.of(2024, 6, 15, 1, 0, 0).format(formatterLDT)

            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "INTERVAL=3;FREQ=MONTHLY;BYDAY=3SA", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val startLocalDateTime = LocalDateTime.of(2024, 9, 15, 1, 0, 0)
            val endLocalDateTime = startLocalDateTime.plusWeeks(1).minusNanos(1)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, true)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("1/1 » PrintMe «", text.trimEnd())
        }

        @Test
        fun `Draw only should yield the correct box`() {
            val entryLocalDateTimeString = LocalDateTime.of(2024, 6, 15, 1, 0, 0).format(formatterLDT)

            val preferencesV1 =
                minRandom<PreferencesV1>().apply {
                    activeProfile = 1
                    rruleSets =
                        setOf(
                            RRuleSetV1(
                                profileId = 1, id = 2, description = "PrintMe",
                                rrule = "INTERVAL=3;FREQ=MONTHLY;BYDAY=3SA", fromLDT = entryLocalDateTimeString,
                            ),
                        )
                }
            val preferenceStore = PreferencesStore(mockk(), preferencesV1)
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val startLocalDateTime = LocalDateTime.of(2024, 9, 15, 1, 0, 0)
            val endLocalDateTime = startLocalDateTime.plusWeeks(1).minusNanos(1)

            writeSuggestions(doc, page, 0f, 0f, startLocalDateTime, endLocalDateTime, preferenceStore, false)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("", text.trimEnd())
        }
    }

    class Header {
        @Test
        fun `Expect weeknumber to be set correctly`() {
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val fromLDT = LocalDateTime.of(2024, 9, 15, 1, 0, 0)

            val box = writeHeader(doc, page, fromLDT)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("Weekplanner 37", text.trimEnd())
            // Check the box
            assertEquals(367f, floor(box.topLeftX))
            assertEquals(565f, floor(box.topLeftY))
            assertEquals(474f, floor(box.bottomRightX))
            assertEquals(552f, floor(box.bottomRightY))
        }

        @Test
        fun `Expect nothing`() {
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val fromLDT = LocalDateTime.of(2024, 9, 15, 1, 0, 0)

            writeHeader(doc, page, fromLDT, false)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("", text.trimEnd())
        }
    }

    class WriteTimeColumn {
        @Test
        fun `Expect a couple of times to be drawn`() {
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val box = writeTimeColumn(doc, page, 0f, 0f, 3, 3, 3)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("03:03\n03:33\n04:03\n04:33", text.trimEnd())
            // Check the box
            assertEquals(0f, floor(box.topLeftX))
            assertEquals(0f, floor(box.topLeftY))
            assertEquals(27f, floor(box.bottomRightX))
            assertEquals(-99f, box.bottomRightY)
        }

        @Test
        fun `Expect nothing`() {
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val box = writeTimeColumn(doc, page, 0f, 0f, 3, 3, 3, false)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("", text.trimEnd())
        }
    }

    class WriteNoteColumn {
        @Test
        fun `Expect a couple of times to be drawn`() {
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val box = writeNoteColumn(doc, page, 0f, 0f, 3, DayOfWeek.MONDAY)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals(
                "maandag\n.........................................\n" +
                    ".........................................\n" +
                    ".........................................\n" +
                    ".........................................",
                text.trimEnd(),
            )

            // Check the box
            assertEquals(0f, floor(box.topLeftX))
            assertEquals(0f, floor(box.topLeftY))
            assertEquals(95f, floor(box.bottomRightX))
            assertEquals(-99f, box.bottomRightY)
        }

        @Test
        fun `Expect nothing to be drawn`() {
            val page = getRotatedA4Page()
            val doc = getDocumentOf(page)

            val box = writeNoteColumn(doc, page, 0f, 0f, 3, DayOfWeek.MONDAY, false)

            val pdfTextStripper = PDFTextStripper()
            val text = pdfTextStripper.getText(doc)
            assertEquals("", text.trimEnd())

            // Check the box
            assertEquals(0f, floor(box.topLeftX))
            assertEquals(0f, floor(box.topLeftY))
            assertEquals(95f, floor(box.bottomRightX))
            assertEquals(-99f, box.bottomRightY)
        }
    }
}
