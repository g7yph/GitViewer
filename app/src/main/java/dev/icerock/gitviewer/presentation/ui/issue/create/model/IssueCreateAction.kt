package dev.icerock.gitviewer.presentation.ui.issue.create.model

internal sealed interface IssueCreateAction {
    data object ShowIssueCreateFailedDialog : IssueCreateAction

    data object OpenPreviousScreen : IssueCreateAction
}