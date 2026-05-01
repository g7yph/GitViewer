package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto

interface IssueRepository {
    suspend fun getAllRepositoryIssues(repoOwner: String, repoName: String): Result<List<IssueDto>>

    suspend fun createIssue(
        repoOwner: String,
        repoName: String,
        issueInput: IssueInputDto
    ): Result<IssueDto>

    suspend fun getIssue(
        repoOwner: String,
        repoName: String,
        number: Int
    ): Result<IssueDto>
}