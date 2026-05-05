package dev.icerock.gitviewer.data.repository

import androidx.paging.PagingData
import dev.icerock.gitviewer.data.Issue
import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    fun getAllRepositoryIssues(
        repoId: Long,
        repoOwner: String,
        repoName: String
    ): Flow<PagingData<Issue>>

    suspend fun createIssue(
        repoId: Long,
        repoOwner: String,
        repoName: String,
        issueInput: IssueInputDto
    ): Result<Unit>

    suspend fun getIssue(number: Long): Result<Issue>
}