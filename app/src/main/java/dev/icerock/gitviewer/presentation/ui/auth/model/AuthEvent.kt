package dev.icerock.gitviewer.presentation.ui.auth.model

internal sealed interface AuthEvent {
    class TokenChanged(val token: String) : AuthEvent

    data object SignIn : AuthEvent
}