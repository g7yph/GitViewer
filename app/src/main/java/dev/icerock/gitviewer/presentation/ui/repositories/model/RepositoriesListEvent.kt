package dev.icerock.gitviewer.presentation.ui.repositories.model

internal sealed interface RepositoriesListEvent {
    class Repository(val id: Long, val owner: String, val name: String) : RepositoriesListEvent

    data object SignOut : RepositoriesListEvent
}