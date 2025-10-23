package screens.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex

@Composable
fun PopupBox(
    popupWidth: Float,
    popupHeight: Float,
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showPopup) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(10F),
            contentAlignment = Alignment.Center,
        ) {
            Column(Modifier.height(60.dp)) {
                // Make sure the Ga terug naar Start button is visible
            }
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { onClickOutside() },
            ) {
                Box(
                    Modifier
                        .width(popupWidth.dp)
                        .height(popupHeight.dp)
                        .background(MaterialTheme.colors.background)
                        .clip(
                            RoundedCornerShape(6.dp),
                        ).shadow(1.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    content()
                }
            }
        }
    }
}
