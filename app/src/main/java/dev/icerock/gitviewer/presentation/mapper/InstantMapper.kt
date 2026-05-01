package dev.icerock.gitviewer.presentation.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

internal fun Instant.toDateString(): String {
    val localDateTime = toLocalDateTime(TimeZone.currentSystemDefault())
    val day = localDateTime.day

    val monthRaw = localDateTime.month.name
    val monthShort = monthRaw.lowercase().replaceFirstChar { it.uppercase() }.take(3)

    return "$day $monthShort"
}