package dev.icerock.gitviewer.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class IssueInputDto(
    val title: String,
    @SerialName("body") val description: String? = null
)