package com.hussainmukadam.nearbyrestaurants

import android.app.Application
import com.hussainmukadam.nearbyrestaurants.util.isOnline

class NearbyRestaurantsApp : Application() {
    companion object {
        lateinit var appInstance: NearbyRestaurantsApp
    }

    override fun onCreate() {
        appInstance = this
        super.onCreate()
    }

    fun isInternetConnected(): Boolean {
        return isOnline(this)
    }
}