package dev.icerock.gitviewer.presentation.ui.main.model

internal sealed interface MainEvent {
    data object CheckAuth : MainEvent
}