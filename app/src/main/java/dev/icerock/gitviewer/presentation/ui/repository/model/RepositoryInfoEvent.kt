package dev.icerock.gitviewer.presentation.ui.repository.model

internal sealed interface RepositoryInfoEvent {
    class FetchRepository(val owner: String, val name: String) : RepositoryInfoEvent

    data object FetchRepositoryReadme : RepositoryInfoEvent

    data object ViewIssues : RepositoryInfoEvent

    data object SignOut : RepositoryInfoEvent

    data object Back : RepositoryInfoEvent
}