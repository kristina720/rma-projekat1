package com.example.projekat.cats.domain

import com.example.projekat.cats.api.model.Weight

data class Cat(
    val name: String,
    val id: String,
    val alternativeNames: String,
    var description: String,
    var temperament: String,
    val origin: String,
    val lifeSpan: String,
    val weight: Weight,
    val rare: Int,

    val adaptability: Int,
    val childFriendly: Int,
    val dogFriendly: Int,
    val strangerFriendly: Int,
    val healthIssues: Int,
    val intelligence: Int,

    val wikipediaURL: String,

    val referenceImageId: String,

)
