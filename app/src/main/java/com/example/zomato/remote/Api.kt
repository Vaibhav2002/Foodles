package com.example.zomato.remote

import com.example.zomato.data.CategoryResponse
import com.example.zomato.data.CollectionResponse
import com.example.zomato.data.NearbyResponse
import com.example.zomato.data.SearchResponse
import com.example.zomato.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val sortBy = "rating"


interface Api {


    @GET("collections")
    suspend fun getCollections(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("user-key") key: String = API_KEY
    ): Response<CollectionResponse>


    @GET("categories")
    suspend fun getCategories(
        @Header("user-key") key: String = API_KEY
    ): Response<CategoryResponse>

    @GET("geocode")
    suspend fun getNearbyRestaurants(
        @Header("user-key") key: String = API_KEY,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<NearbyResponse>

    @GET("search")
    suspend fun searchRestaurantByCuisinePaging(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("q") cuisine: String,
        @Query("start") start: Int,
        @Query("sort") sort: String = sortBy,
        @Header("user-key") key: String = API_KEY
    ): SearchResponse

    @GET("search")
    suspend fun searchRestaurantByCategoryPaging(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("category") category: String,
        @Query("start") start: Int,
        @Query("sort") sort: String = sortBy,
        @Header("user-key") key: String = API_KEY
    ): SearchResponse

    @GET("search")
    suspend fun searchRestaurantByCollectionPaging(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("collection_id") id: String,
        @Query("start") start: Int,
        @Query("sort") sort: String = sortBy,
        @Header("user-key") key: String = API_KEY
    ): SearchResponse

    @GET("search")
    suspend fun searchRestaurantByQueryPaging(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("q") searchQuery: String,
        @Query("start") start: Int,
        @Query("sort") sort: String = sortBy,
        @Header("user-key") key: String = API_KEY
    ): SearchResponse
}
