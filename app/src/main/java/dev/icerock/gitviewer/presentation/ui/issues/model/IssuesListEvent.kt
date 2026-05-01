package dev.icerock.gitviewer.presentation.ui.issues.model

internal sealed interface IssuesListEvent {
    class FetchIssues(val repoOwner: String, val repoName: String) : IssuesListEvent

    class Issue(val number: Int) : IssuesListEvent

    data object CreateIssue : IssuesListEvent

    data object Back : IssuesListEvent
}