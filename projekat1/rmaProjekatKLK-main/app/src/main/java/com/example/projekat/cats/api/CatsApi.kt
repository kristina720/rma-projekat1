package com.example.projekat.cats.api

import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.api.model.ImageModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatsApi {

    @GET("breeds")
    suspend fun getAllCats(): List<CatsApiModel>

    @GET("breeds/{id}")
    suspend fun getCat(
        @Path("id") catId: String,
    ): CatsApiModel

    @GET("images/{id}")
    suspend fun getImage(
        @Path("id") imageId: String,
    ): ImageModel

    @GET("breeds/search")
    suspend fun getSearch(
        @Query("q") query: String,
    ): List<CatsApiModel>


}