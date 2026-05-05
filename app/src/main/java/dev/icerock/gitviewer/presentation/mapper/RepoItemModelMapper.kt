package dev.icerock.gitviewer.presentation.mapper

import dev.icerock.gitviewer.data.Repository
import dev.icerock.gitviewer.presentation.model.RepoItemModel

internal fun Repository.toRepoItemModel(): RepoItemModel {
    return RepoItemModel(
        id = id,
        owner = owner,
        name = name,
        description = description,
        primaryLanguage = language,
        link = url,
        license = license,
        stars = stars_count.toInt(),
        forks = forks_count.toInt(),
        watchers = watchers_count.toInt(),
        issues = issues_count.toInt()
    )
}