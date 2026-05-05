package dev.icerock.gitviewer.presentation.mapper

import dev.icerock.gitviewer.data.Issue
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel
import kotlin.time.Instant

internal fun Issue.toIssueItemModel(): IssueItemModel {
    return IssueItemModel(
        id = id,
        number = number.toInt(),
        name = title,
        state = when (state) {
            "open" -> IssueStateModel.Open

            "closed" -> IssueStateModel.Closed

            else -> IssueStateModel.Closed
        },
        date = Instant.parse(created_at).toDateString(),
        description = description
    )
}