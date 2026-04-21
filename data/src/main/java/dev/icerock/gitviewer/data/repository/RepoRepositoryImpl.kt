package dev.icerock.gitviewer.data.repository

import android.util.Base64
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto

internal class RepoRepositoryImpl(private val gitHubApiService: GitHubApiService) : RepoRepository {
    override suspend fun getAllRepositories(): Result<List<RepoDto>> {
        return gitHubApiService.getAllRepositories()
    }

    override suspend fun getRepository(owner: String, name: String): Result<RepoDto> {
        return gitHubApiService.getRepository(owner = owner, name = name)
    }

    override suspend fun getRepositoryReadme(owner: String, name: String): Result<String> {
        return gitHubApiService.getRepositoryReadme(owner = owner, name = name).map {
            val bytes = Base64.decode(it.content, Base64.DEFAULT)
            String(bytes, Charsets.UTF_8)
        }
    }
}