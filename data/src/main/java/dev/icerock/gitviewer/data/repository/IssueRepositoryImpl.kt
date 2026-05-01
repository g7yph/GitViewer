package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto

internal class IssueRepositoryImpl(private val gitHubApiService: GitHubApiService) : IssueRepository {
    override suspend fun getAllRepositoryIssues(repoOwner: String, repoName: String): Result<List<IssueDto>> {
        return gitHubApiService.getRepositoryIssues(owner = repoOwner, name = repoName)
    }

    override suspend fun createIssue(
        repoOwner: String,
        repoName: String,
        issueInput: IssueInputDto
    ): Result<IssueDto> {
        return gitHubApiService.createIssue(
            owner = repoOwner,
            name = repoName,
            input = issueInput
        )
    }

    override suspend fun getIssue(
        repoOwner: String,
        repoName: String,
        number: Int
    ): Result<IssueDto> {
        return gitHubApiService.getIssue(
            owner = repoOwner,
            name = repoName,
            issueNumber = number
        )
    }
}