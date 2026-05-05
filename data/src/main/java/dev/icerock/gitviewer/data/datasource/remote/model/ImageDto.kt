package dev.icerock.gitviewer.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
class ImageDto(
    val data: ImageDataDto,
    val success: Boolean,
    val status: Int
)