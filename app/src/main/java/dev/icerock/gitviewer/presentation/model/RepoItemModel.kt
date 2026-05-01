package dev.icerock.gitviewer.presentation.model

internal class RepoItemModel(
    val id: Long,
    val owner: String,
    val name: String,
    val description: String?,
    val primaryLanguage: String?,
    val link: String,
    val license: String?,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val issues: Int
)