package com.hussainmukadam.nearbyrestaurants.model


import com.google.gson.annotations.SerializedName

data class GeocodeSearchResponse(
    @SerializedName("location")
    val location: LocationResponse,
    @SerializedName("top_cuisines")
    val topCuisines: List<String>,
    @SerializedName("nearby_restaurants")
    val nearbyRestaurants: List<NearbyRestaurants>
)

data class LocationResponse(
    @SerializedName("entity_id")
    val entityId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("country_name")
    val countryName: String
)

data class NearbyRestaurants(
    @SerializedName("restaurant")
    val restaurant: Restaurant
)

data class Restaurant(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("cuisines")
    val cuisines: String,
    @SerializedName("thumb")
    val thumb: String,
    @SerializedName("currency")
    private val currency: String,
    @SerializedName("average_cost_for_two")
    private val averageCostForTwo: Int,
    @SerializedName("user_rating")
    val userRating: UserRating,
    @SerializedName("location")
    val location: RestaurantLocation
) {
    val getAvgCostWithCurrency: String
        get() = "$currency$averageCostForTwo/-"
}

data class RestaurantLocation(
    @SerializedName("locality")
    val locality: String,
    @SerializedName("locality_verbose")
    val localityVerbose: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("zipcode")
    val zipcode: String,
    @SerializedName("address")
    val address: String
)

data class UserRating(
    @SerializedName("aggregate_rating")
    val aggregateRating: String,
    @SerializedName("rating_color")
    private var ratingColor: String,
    @SerializedName("rating_text")
    val ratingText: String
) {
    val hexColor: String
        get() = "#$ratingColor"
}