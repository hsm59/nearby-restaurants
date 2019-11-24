package com.hussainmukadam.nearbyrestaurants.viewmodel

import androidx.lifecycle.ViewModel
import com.hussainmukadam.nearbyrestaurants.network.ApiService
import com.hussainmukadam.nearbyrestaurants.repository.NearbyRestaurantsRepo

class NearbyRestaurantsViewModel : ViewModel() {
    private var nearbyRestaurantsRepo: NearbyRestaurantsRepo = NearbyRestaurantsRepo(ApiService.create())

    fun fetchNearbyListing(lat: Double, long: Double)
            = nearbyRestaurantsRepo.fetchNearbyRestaurants(lat, long)
}