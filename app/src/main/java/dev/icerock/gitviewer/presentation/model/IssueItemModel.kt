package dev.icerock.gitviewer.presentation.model

internal class IssueItemModel(
    val id: Long,
    val number: Int,
    val name: String,
    val state: IssueStateModel,
    val date: String,
    val description: String?,
)