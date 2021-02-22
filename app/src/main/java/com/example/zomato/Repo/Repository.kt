package com.example.zomato.Repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.zomato.remote.Api
import com.example.zomato.util.SearchParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class Repository @Inject constructor(private val api: Api) {


    suspend fun getCollections(lat: Double, lon: Double) =
        withContext(Dispatchers.IO) {
            api.getCollections(lat, lon)
        }

    suspend fun getCategories() = withContext(Dispatchers.IO) {
        Timber.d(api.getCategories().body()?.categories.toString())
        api.getCategories()
    }

    suspend fun getNearbyRestaurants(lat: Double, lon: Double) = withContext(Dispatchers.IO) {
        api.getNearbyRestaurants(lat = lat, lon = lon)
    }

    fun searchByPaging(
        lat: Double,
        lon: Double,
        id: String,
        searchParameter: SearchParameter
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PagingClass(api, lat, lon, id, searchParameter) }
    ).liveData

}