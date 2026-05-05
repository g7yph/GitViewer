package dev.icerock.gitviewer.data.datasource.remote

import dev.icerock.gitviewer.data.datasource.remote.model.ImageDto
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

internal interface ImageBBApiService {
    @POST("upload?key=1fb7a6f9214bbd34432e304299dabf86")
    @Multipart
    fun uploadImage(@Part("image") image: File): Result<ImageDto>
}