package dev.icerock.gitviewer.presentation.ui.issue.info.model

internal sealed interface IssueInfoAction {
    data object OpenPreviousScreen : IssueInfoAction
}