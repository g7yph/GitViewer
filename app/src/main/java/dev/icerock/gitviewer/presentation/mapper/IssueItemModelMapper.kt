package dev.icerock.gitviewer.presentation.mapper

import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel

internal fun IssueDto.toIssueItemModel(): IssueItemModel {
    return IssueItemModel(
        id = id,
        number = number,
        name = name,
        state = when (state) {
            "open" -> IssueStateModel.Open

            "closed" -> IssueStateModel.Closed

            else -> IssueStateModel.Closed
        },
        date = date.toDateString(),
        description = description
    )
}