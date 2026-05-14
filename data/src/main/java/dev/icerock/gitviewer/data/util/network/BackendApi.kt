package dev.icerock.gitviewer.data.util.network

internal enum class BackendApi(val value: String) {
    GitHub("https://api.github.com"),
    Attachments("https://api.imgbb.com")
}