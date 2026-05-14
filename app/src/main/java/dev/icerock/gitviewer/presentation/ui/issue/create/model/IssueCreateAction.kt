package dev.icerock.gitviewer.presentation.ui.issue.create.model

internal sealed interface IssueCreateAction {
    data object ShowActionFailedDialog : IssueCreateAction

    data object OpenPreviousScreen : IssueCreateAction
}