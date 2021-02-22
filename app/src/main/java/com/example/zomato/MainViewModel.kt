package com.example.zomato

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.zomato.Repo.Repository
import com.example.zomato.data.*
import com.example.zomato.util.InternetCheck
import com.example.zomato.util.NO_INTERNET
import com.example.zomato.util.Resource
import com.example.zomato.util.SearchParameter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class MainViewModel @ViewModelInject constructor(
    private val repo: Repository,
    private val isConnected: InternetCheck
) : ViewModel() {
    //private members
    private var _collect: MutableLiveData<Resource<CollectionResponse>> = MutableLiveData()
    private val _categories: MutableLiveData<Resource<CategoryResponse>> = MutableLiveData()
    private val _nearby: MutableLiveData<Resource<NearbyResponse>> = MutableLiveData()
    private val lat = 22.5674
    private val lon = 88.3639
    private val _location: MutableLiveData<Location> = MutableLiveData()
    val query: MutableLiveData<Pair<String, SearchParameter>> =
        MutableLiveData(Pair(" ", SearchParameter.COLLECTION))
    private var _searchByPaging = query.switchMap {
        repo.searchByPaging(
            _location.value?.latitude ?: lat,
            _location.value?.longitude ?: lon,
            it.first,
            it.second
        ).cachedIn(viewModelScope)
    }



    private val _event: Channel<Events> = Channel()
    val event get() = _event.receiveAsFlow()

    //public members
    val collect get() = _collect
    val categories get() = _categories
    val nearby get() = _nearby
    val searchByPaging get() = _searchByPaging
    val location get() = _location

    fun setLocation(location: Location) {
        _location.value = location
        _location.value.let {
            getNearby(_location.value!!.latitude, _location.value!!.longitude)
            getCategories()
            getCollections(_location.value!!.latitude, _location.value!!.longitude)
        }

    }

    fun refetchDataForNearbyPage() {
        getNearby(_location.value!!.latitude, _location.value!!.longitude)
        getCategories()
    }

    fun refetchDataForCollectionPage() {
        getCollections(_location.value!!.latitude, _location.value!!.longitude)
    }

    private fun getCategories() {
        viewModelScope.launch {
            if (isConnected.isConnected()) {
                val response = repo.getCategories()
                if (response.isSuccessful) {
                    response.body().let {
                        _categories.postValue(Resource.Success(response.body()!!))
                    }
                } else _categories.postValue(Resource.Error(response.message()))
            }
        }

    }

    private fun getCollections(lat: Double, lon: Double) {
        _collect.postValue(Resource.Loading())
        viewModelScope.launch {
            if (isConnected.isConnected()) {
                val response = repo.getCollections(lat, lon)
                if (response.isSuccessful) {
                    response.body().let {
                        _collect.postValue(Resource.Success(response.body()!!))
                    }
                } else _collect.postValue(Resource.Error(response.message()))
            } else _collect.postValue(Resource.Error(NO_INTERNET))
        }
    }

    private fun getNearby(lat: Double, lon: Double) {
        _nearby.postValue(Resource.Loading())
        viewModelScope.launch {
            if (isConnected.isConnected()) {
                val response = repo.getNearbyRestaurants(lat, lon)
                if (response.isSuccessful) {
                    response.body().let {
                        _nearby.postValue(Resource.Success(response.body()!!))
                    }
                } else _nearby.postValue(Resource.Error(response.message()))
            } else _nearby.postValue(Resource.Error(NO_INTERNET))
        }

    }

    fun cuisineClicked(cuisine: String) = viewModelScope.launch {
        _event.send(Events.ListByCuisine(cuisine))
    }

    fun categoryCLicked(category: Category) = viewModelScope.launch {
        _event.send(Events.ListByCategory(category))
    }

    fun restaurantClicked(restaurantXSearch: RestaurantXSearch) = viewModelScope.launch {
        _event.send(Events.ShowRestaurantDetail(restaurantXSearch))
    }

    fun collectionClicked(collections: Collections) = viewModelScope.launch {
        _event.send(Events.ListByCollection(collections))
    }

    fun checkInternet(): Boolean {
        if (!isConnected.isConnected()) {
            internetError()
            return false
        }
        return true
    }

    fun internetError() = viewModelScope.launch {
        _event.send(Events.ShowInternetError)
    }


    sealed class Events {
        data class ListByCollection(val collections: Collections) : Events()
        data class ListByCategory(val category: Category) : Events()
        data class ListByCuisine(val cuisine: String) : Events()
        data class ShowRestaurantDetail(val restaurantXSearch: RestaurantXSearch) : Events()
        object ShowInternetError : Events()
    }

}


