package dev.icerock.gitviewer.presentation.ui.issue.create.model

internal sealed interface IssueCreateEvent {
    class TitleChanged(val title: String) : IssueCreateEvent

    class DescriptionChanged(val description: String) : IssueCreateEvent

    class SubmitIssue(val repoId: Long, val repoOwner: String, val repoName: String) : IssueCreateEvent

    data object Back : IssueCreateEvent
}