package dev.icerock.gitviewer.presentation.ui.issue.info.model

internal sealed interface IssueInfoEvent {
    class FetchIssue(val repoOwner: String, val repoName: String, val number: Int) : IssueInfoEvent

    data object Back : IssueInfoEvent
}