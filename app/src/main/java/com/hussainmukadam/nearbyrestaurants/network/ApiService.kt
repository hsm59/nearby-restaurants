package com.hussainmukadam.nearbyrestaurants.network

import androidx.lifecycle.MutableLiveData
import com.hussainmukadam.nearbyrestaurants.BuildConfig
import com.hussainmukadam.nearbyrestaurants.model.GeocodeSearchResponse
import com.hussainmukadam.nearbyrestaurants.util.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("geocode")
    fun getNearbyRestaurants(
        @Query("lat") lat: Double,
        @Query("lon") long: Double
    ): MutableLiveData<Resource<GeocodeSearchResponse>>

    companion object Factory {
        fun create(): ApiService {
            val client = OkHttpClient().newBuilder()
                .addInterceptor(ConnectivityInterceptor())
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .addInterceptor {
                    it.run {
                        proceed(
                            request()
                                .newBuilder()
                                .addHeader("user-key", "4feaa2167c4dc6beadf629319423bd4b")
                                .build()
                        )
                    }
                }
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .baseUrl(BASE_URL)
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}