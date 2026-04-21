package dev.icerock.gitviewer.presentation.mapper

import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import dev.icerock.gitviewer.presentation.model.RepoItemModel

internal fun RepoDto.toRepoModel(): RepoItemModel {
    return RepoItemModel(
        id = id,
        owner = owner.login,
        name = name,
        description = description,
        primaryLanguage = language,
        link = htmlUrl,
        license = license?.name,
        stars = stargazersCount,
        forks = forksCount,
        watchers = watchersCount
    )
}