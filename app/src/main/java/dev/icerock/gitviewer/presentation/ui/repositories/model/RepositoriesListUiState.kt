package dev.icerock.gitviewer.presentation.ui.repositories.model

import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.RepoItemModel

internal data class RepositoriesListUiState(
    val isLoading: Boolean = true,
    val error: ErrorTypeModel = ErrorTypeModel.Unknown(message = ""),
    val repos: List<RepoItemModel>? = null
)