package dev.icerock.gitviewer.presentation.ui.issue.info.model

import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.IssueItemModel

internal data class IssueInfoUiState(
    val isIssueLoading: Boolean = true,
    val issueError: ErrorTypeModel = ErrorTypeModel.Unknown(message = ""),
    val issue: IssueItemModel? = null
)