package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.remote.ImageBBApiService
import dev.icerock.gitviewer.data.datasource.remote.model.ImageDto
import java.io.File

internal class ImageRepositoryImpl(private val imageBBApiService: ImageBBApiService) : ImageRepository {
    override suspend fun uploadImage(image: File): Result<ImageDto> {
        return imageBBApiService.uploadImage(image = image)
    }
}