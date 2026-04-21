package dev.icerock.gitviewer.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RepoDto(
    val id: Long,
    val name: String,
    val private: Boolean,
    val owner: OwnerDto,
    @SerialName("html_url") val htmlUrl: String,
    val description: String?,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    val language: String?,
    @SerialName("forks_count") val forksCount: Int,
    val license: LicenseDto?
)