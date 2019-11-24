package com.hussainmukadam.nearbyrestaurants.network

import com.hussainmukadam.nearbyrestaurants.NearbyRestaurantsApp
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NearbyRestaurantsApp.appInstance.isInternetConnected()) {
            throw ConnectivityException()
        } else {
            return chain.proceed(chain.request())
        }
    }
}