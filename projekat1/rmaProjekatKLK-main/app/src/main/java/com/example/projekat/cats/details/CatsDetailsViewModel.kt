package com.example.projekat.cats.details

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.cats.list.CatsListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import com.example.projekat.cats.repository.CatsRepository
import java.io.IOException


class CatsDetailsViewModel(
    private val catId: String,
    private val repository: CatsRepository = CatsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatsDetailsState(catId = catId))
    val state = _state.asStateFlow()
    private fun setState(reducer: CatsDetailsState.() -> CatsDetailsState) =
        _state.getAndUpdate(reducer)

    init {
        fetchCatDetails()
    }

    @SuppressLint("SuspiciousIndentation")     //ignorise upozorenja
    private fun fetchCatDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(fetching = true)
            try {
                val catDetails = repository.fetchCatDetails(catId = catId).specificCat()
                    setState { copy(catId = catDetails.id) }
                    setState { copy(data = catDetails) }
                fetchImage(catDetails.referenceImageId)
            } catch (error: IOException) {
                _state.value = _state.value.copy(error = CatsListState.ListError.ListUpdateFailed(cause = error))
            } finally {
                _state.value = _state.value.copy(fetching = false)
            }
        }
    }

    private fun fetchImage(referenceImageId: String){
        viewModelScope.launch {
            _state.value = _state.value.copy(fetching = true)
            try {
                val imageModel = repository.fetchImage(imageId = referenceImageId)
                setState { copy(imageModel = imageModel) }
            } catch (error: IOException) {
                _state.value = _state.value.copy(error = CatsListState.ListError.ListUpdateFailed(cause = error))
            } finally {
                _state.value = _state.value.copy(fetching = false)
            }
        }
    }
    private fun CatsApiModel.specificCat() = Cat(
        id = this.id,

        name = this.name,
        alternativeNames = this.alternativeNames,
        description = this.description,
        temperament = this.temperament,
        origin = this.origin,
        lifeSpan = this.lifeSpan,
        weight = this.weight,
        rare = this.rare,

        adaptability = this.adaptability,
        childFriendly = this.childFriendly,
        dogFriendly = this.dogFriendly,
        strangerFriendly = this.strangerFriendly,
        healthIssues = this.healthIssues,
        intelligence = this.intelligence,

        wikipediaURL = this.wikipediaURL,
        referenceImageId = this.referenceImageId
    )
}
