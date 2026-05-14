package dev.icerock.gitviewer.data.datasource.remote

import dev.icerock.gitviewer.data.datasource.remote.model.ImageDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface ImageBBApiService {
    @POST("1/upload?key=1fb7a6f9214bbd34432e304299dabf86")
    @Multipart
    suspend fun uploadImage(@Part image: MultipartBody.Part): Result<ImageDto>
}