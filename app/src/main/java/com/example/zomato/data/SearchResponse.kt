package com.example.zomato.data

import java.io.Serializable

data class SearchResponse(
    val restaurants: List<RestaurantSearch>,
    val results_found: Int,
    val results_shown: Int,
    val results_start: Int
)

data class RestaurantSearch(
    val restaurant: RestaurantXSearch
) : Serializable

data class RestaurantXSearch(
    val average_cost_for_two: Int = 0,
    val cuisines: String = "",
    val id: String = "",
    val location: LocationX,
    val name: String = "",
    val currency: String = "",
    val phone_numbers: String? = "Not available",
    val thumb: String = "",
    val featured_image: String = "",
    val timings: String = "not available",
    val url: String = "",
    val user_rating: UserRating
) : Serializable
