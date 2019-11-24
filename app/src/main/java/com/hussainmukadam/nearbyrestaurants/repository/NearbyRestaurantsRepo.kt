package com.hussainmukadam.nearbyrestaurants.repository

import androidx.lifecycle.MutableLiveData
import com.hussainmukadam.nearbyrestaurants.model.GeocodeSearchResponse
import com.hussainmukadam.nearbyrestaurants.network.ApiService
import com.hussainmukadam.nearbyrestaurants.network.Resource

/**
 * Repository abstracts the logic of fetching the data and persisting it for
 * offline. They are the data source as the single source of truth.
 */

class NearbyRestaurantsRepo(private val apiService: ApiService) {
    fun fetchNearbyRestaurants(lat: Double, long: Double)
            : MutableLiveData<Resource<GeocodeSearchResponse>> = apiService.getNearbyRestaurants(lat, long)
}