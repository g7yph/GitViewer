package dev.icerock.gitviewer.presentation.ui.repository.model

internal sealed interface RepositoryInfoEvent {
    class FetchRepository(val id: Long) : RepositoryInfoEvent

    data object FetchRepositoryReadme : RepositoryInfoEvent

    data object ViewIssues : RepositoryInfoEvent

    data object SignOut : RepositoryInfoEvent

    data object Back : RepositoryInfoEvent
}