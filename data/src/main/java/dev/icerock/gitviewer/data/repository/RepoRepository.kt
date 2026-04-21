package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import dev.icerock.gitviewer.data.datasource.remote.model.RepoReadmeDto

interface RepoRepository {
    suspend fun getAllRepositories(): Result<List<RepoDto>>

    suspend fun getRepository(owner: String, name: String): Result<RepoDto>

    suspend fun getRepositoryReadme(owner: String, name: String): Result<String>
}