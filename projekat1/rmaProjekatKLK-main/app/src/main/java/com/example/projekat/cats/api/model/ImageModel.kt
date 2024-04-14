package com.example.projekat.cats.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageModel (

    val id: String,
    val width: Int,
    val height: Int,
    val url: String

)