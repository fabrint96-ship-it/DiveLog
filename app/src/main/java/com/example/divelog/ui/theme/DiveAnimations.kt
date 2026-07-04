package com.example.divelog.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween

object DiveAnimations {

    const val Fast = 180
    const val Medium = 260
    const val Slow = 420

    fun fadeInMedium(): EnterTransition =
        fadeIn(animationSpec = tween(Medium))

    fun fadeOutFast(): ExitTransition =
        fadeOut(animationSpec = tween(Fast))

    fun cardEnter(): EnterTransition =
        fadeIn(animationSpec = tween(Medium)) +
                slideInVertically(
                    animationSpec = tween(Medium),
                    initialOffsetY = { it / 3 }
                )

    fun cardExit(): ExitTransition =
        fadeOut(animationSpec = tween(Fast)) +
                slideOutVertically(
                    animationSpec = tween(Fast),
                    targetOffsetY = { -it / 4 }
                )

    fun scaleEnter(): EnterTransition =
        fadeIn(animationSpec = tween(Medium)) +
                scaleIn(
                    animationSpec = tween(Medium),
                    initialScale = 0.96f
                )

    fun scaleExit(): ExitTransition =
        fadeOut(animationSpec = tween(Fast)) +
                scaleOut(
                    animationSpec = tween(Fast),
                    targetScale = 1.04f
                )
}