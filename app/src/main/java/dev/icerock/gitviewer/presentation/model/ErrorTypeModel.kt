package dev.icerock.gitviewer.presentation.model

internal sealed interface ErrorTypeModel {
    data object NoInternet : ErrorTypeModel

    class Unknown(val message: String) : ErrorTypeModel
}