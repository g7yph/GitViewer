package dev.icerock.gitviewer.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
class RepoReadmeDto(
    val content: String
)