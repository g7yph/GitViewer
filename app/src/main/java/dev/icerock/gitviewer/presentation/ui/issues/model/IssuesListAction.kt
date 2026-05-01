package dev.icerock.gitviewer.presentation.ui.issues.model

internal sealed interface IssuesListAction {
    data object OpenIssueCreateScreen : IssuesListAction

    class OpenIssueInfoScreen(val number: Int) : IssuesListAction

    data object OpenPreviousScreen : IssuesListAction
}