package dev.icerock.gitviewer.presentation.ui.issues.model

import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.IssueItemModel

internal data class IssuesListUiState(
    val isLoading: Boolean = true,
    val error: ErrorTypeModel = ErrorTypeModel.Unknown(""),
    val issues: List<IssueItemModel>? = null
)