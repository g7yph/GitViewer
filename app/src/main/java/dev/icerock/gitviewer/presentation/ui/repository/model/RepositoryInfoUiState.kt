package dev.icerock.gitviewer.presentation.ui.repository.model

import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.RepoItemModel

internal data class RepositoryInfoUiState(
    val isRepoLoading: Boolean = true,
    val repoError: ErrorTypeModel = ErrorTypeModel.Unknown(message = ""),
    val repo: RepoItemModel? = null,
    val isRepoReadmeLoading: Boolean = true,
    val repoReadmeError: ErrorTypeModel = ErrorTypeModel.Unknown(message = ""),
    val repoReadme: String = "",
)