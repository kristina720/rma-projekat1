package com.example.projekat.cats.details

import com.example.projekat.cats.api.model.ImageModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.cats.list.CatsListState


data class CatsDetailsState(
    val catId: String,
    val imageModel: ImageModel? = null,
    val fetching: Boolean = false,
    val data: Cat? = null,
    val error: CatsListState.ListError.ListUpdateFailed? = null,
) {
    sealed class DetailsError {
        data class DataUpdateFailed(val cause: Throwable? = null) : DetailsError()
    }
}
