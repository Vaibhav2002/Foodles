package com.example.zomato.Repo

import androidx.paging.PagingSource
import com.example.zomato.data.RestaurantSearch
import com.example.zomato.remote.Api
import com.example.zomato.util.SearchParameter


private const val start_val = 0

class PagingClass constructor(
    private val api: Api,
    private val lat: Double,
    private val lon: Double,
    private val id: String,
    private val searchParameter: SearchParameter
) : PagingSource<Int, RestaurantSearch>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RestaurantSearch> {
        val start = params.key ?: start_val
        return try {
            val response = when (searchParameter) {
                SearchParameter.CUISINE -> api.searchRestaurantByCuisinePaging(lat, lon, id, start)
                SearchParameter.CATEGORY -> api.searchRestaurantByCategoryPaging(
                    lat,
                    lon,
                    id,
                    start
                )
                SearchParameter.COLLECTION -> api.searchRestaurantByCollectionPaging(
                    lat,
                    lon,
                    id,
                    start
                )
                SearchParameter.QUERY -> api.searchRestaurantByQueryPaging(lat, lon, id, start)
            }
            val data = response.restaurants
            LoadResult.Page(
                data = data,
                prevKey = if (start == 0) null else start - 20,
                nextKey = if (data.isEmpty()) null else start + 20
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}