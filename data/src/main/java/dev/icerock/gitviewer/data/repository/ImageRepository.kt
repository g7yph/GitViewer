package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.remote.model.ImageDto
import java.io.File

interface ImageRepository {
    suspend fun uploadImage(image: File): Result<ImageDto>
}