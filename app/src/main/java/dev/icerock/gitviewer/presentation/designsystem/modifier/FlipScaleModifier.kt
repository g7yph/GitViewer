package dev.icerock.gitviewer.presentation.designsystem.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.flipScale(
    state: Boolean,
    duration: Int = 250
): Modifier {
    val iconScaleY = remember {
        Animatable(initialValue = 1f)
    }

    LaunchedEffect(state) {
        iconScaleY.animateTo(
            targetValue = if (state) -1f else 1f,
            animationSpec = TweenSpec(
                durationMillis = duration,
                easing = LinearEasing
            )
        )
    }

    return this then Modifier.graphicsLayer(scaleY = iconScaleY.value)
}