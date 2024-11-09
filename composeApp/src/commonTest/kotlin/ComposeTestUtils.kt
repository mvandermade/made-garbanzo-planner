import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithText

fun ComposeContentTestRule.waitUntilText(
    text: String,
    amount: Int = 1,
) {
    val cr = this
    cr.waitUntil(5_000) {
        cr.onAllNodesWithText(text).fetchSemanticsNodes().size == amount
    }
}
