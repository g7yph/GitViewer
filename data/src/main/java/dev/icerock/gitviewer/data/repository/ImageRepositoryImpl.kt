package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.remote.ImageBBApiService
import dev.icerock.gitviewer.data.datasource.remote.model.ImageDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

internal class ImageRepositoryImpl(private val imageBBApiService: ImageBBApiService) : ImageRepository {
    override suspend fun uploadImage(image: File): Result<ImageDto> {
        val requestFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(
            "image",
            image.name,
            requestFile
        )

        return imageBBApiService.uploadImage(image = body)
    }
}