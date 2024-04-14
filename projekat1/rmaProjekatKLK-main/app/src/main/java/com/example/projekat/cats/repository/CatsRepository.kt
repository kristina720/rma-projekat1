package com.example.projekat.cats.repository

import com.example.projekat.cats.api.CatsApi
import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.api.model.ImageModel
import com.example.projekat.networking.retrofit

object CatsRepository {

    //u view-ovima se pozivaju

    private val catsApi: CatsApi = retrofit.create(CatsApi::class.java)

    suspend fun fetchAllCats(): List<CatsApiModel> = catsApi.getAllCats()

    suspend fun fetchCatDetails(catId: String): CatsApiModel = catsApi.getCat(catId = catId)

    suspend fun fetchImage(imageId: String): ImageModel = catsApi.getImage(imageId = imageId)

    suspend fun searchCatsByName(nameQuery: String): List<CatsApiModel> {
        return catsApi.getSearch(nameQuery)
    }
    

}
