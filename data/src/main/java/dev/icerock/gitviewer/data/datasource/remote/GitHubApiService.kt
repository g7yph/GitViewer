package dev.icerock.gitviewer.data.datasource.remote

import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import dev.icerock.gitviewer.data.datasource.remote.model.RepoReadmeDto
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Path
import retrofit2.http.Query

internal interface GitHubApiService {

    @HEAD("user")
    suspend fun checkAuth(): Result<Unit>

    @GET("user/repos")
    suspend fun getAllRepositories(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc"
    ): Result<List<RepoDto>>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") name: String
    ): Result<RepoDto>

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getRepositoryReadme(
        @Path("owner") owner: String,
        @Path("repo") name: String
    ): Result<RepoReadmeDto>
}