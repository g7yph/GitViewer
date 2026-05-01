package dev.icerock.gitviewer.data.datasource.remote

import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto
import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import dev.icerock.gitviewer.data.datasource.remote.model.RepoReadmeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @GET("repos/{owner}/{repo}/issues")
    suspend fun getRepositoryIssues(
        @Path("owner") owner: String,
        @Path("repo") name: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): Result<List<IssueDto>>

    @POST("/repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") name: String,
        @Body input: IssueInputDto
    ): Result<IssueDto>

    @GET("/repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun getIssue(
        @Path("owner") owner: String,
        @Path("repo") name: String,
        @Path("issue_number") issueNumber: Int
    ): Result<IssueDto>
}