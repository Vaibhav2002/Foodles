package com.example.zomato.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class NearbyResponse(
    @PrimaryKey
    val id: Int,
    val nearby_restaurants: List<NearbyRestaurant>,
    val popularity: Popularity
)

data class NearbyRestaurant(
    val restaurant: RestaurantXSearch
) : Serializable

data class LocationX(
    val address: String,
    val city_id: Int,
    val latitude: String,
    val locality: String,
    val longitude: String,
) : Serializable


data class Popularity(
    val top_cuisines: List<String>
) : Serializable

data class UserRating(
    val aggregate_rating: String,
    val rating_color: String,
    val rating_text: String,
    val votes: Int
) : Serializable