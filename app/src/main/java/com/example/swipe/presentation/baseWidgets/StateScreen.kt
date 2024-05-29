package com.example.swipe.presentation.baseWidgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter

/**
 * Composable function for displaying a state screen with an icon, primary text, and secondary text.
 *
 * This function renders a state screen layout containing an icon, primary text, and secondary text.
 * It is typically used to represent various states such as empty, error, or loading states in the UI.
 *
 * @param modifier The modifier for the state screen layout. Defaults to [Modifier].
 * @param icon The drawable resource ID representing the icon to be displayed on the state screen.
 * @param textPrimary The string resource ID representing the primary text to be displayed on the state screen.
 * @param textSecondary The string resource ID representing the secondary text to be displayed on the state screen.
 */
@Composable
fun StateScreen(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes textPrimary: Int,
    @StringRes textSecondary: Int,
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (iconRef, titleRef, subTitleRef) = createRefs()
        Image(
            modifier = Modifier
                .size(192.dp)
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            painter = rememberAsyncImagePainter(model = icon),
            contentDescription = "Empty feed",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiaryContainer)
        )
        Text(
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(iconRef.bottom, margin = 10.dp)
                start.linkTo(iconRef.start)
                end.linkTo(iconRef.end)
            },
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(id = textPrimary),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.constrainAs(subTitleRef) {
                top.linkTo(titleRef.bottom, margin = 10.dp)
                start.linkTo(titleRef.start)
                end.linkTo(titleRef.end)
            },
            style = MaterialTheme.typography.titleSmall,
            text = stringResource(id = textSecondary),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}