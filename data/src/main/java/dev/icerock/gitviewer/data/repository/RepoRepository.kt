package dev.icerock.gitviewer.data.repository

import androidx.paging.PagingData
import dev.icerock.gitviewer.data.Repository
import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    fun getAllRepositories(): Flow<PagingData<Repository>>

    suspend fun getRepository(id: Long): Result<Repository>

    suspend fun getRepositoryReadme(owner: String, name: String): Result<String>
}