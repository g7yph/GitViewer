package dev.icerock.gitviewer.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
class IssueDto(
    val id: Long,
    val number: Int,
    @SerialName("title") val name: String,
    val state: String,
    @SerialName("created_at") val date: Instant,
    @SerialName("body") val description: String? = null
)