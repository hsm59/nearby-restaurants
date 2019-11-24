package com.hussainmukadam.nearbyrestaurants.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hussainmukadam.nearbyrestaurants.R
import com.hussainmukadam.nearbyrestaurants.model.NearbyRestaurants
import kotlinx.android.synthetic.main.restaurant_list_item.view.*
import java.util.*

class NearbyRestaurantsAdapter(
    private val nearbyRestaurantsList: List<NearbyRestaurants>,
    private val listener: (url: String) -> Unit
) :
    RecyclerView.Adapter<NearbyRestaurantsAdapter.NearbyRestaurantsViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyRestaurantsViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.restaurant_list_item, parent, false)
        return NearbyRestaurantsViewHolder(view)
    }

    override fun getItemCount(): Int = nearbyRestaurantsList.size

    override fun onBindViewHolder(holder: NearbyRestaurantsViewHolder, position: Int) {
        val restaurantInfo = nearbyRestaurantsList[position]
        with(holder.itemView) {
            tv_rest_name.text = restaurantInfo.restaurant.name
            tv_rest_location.text = restaurantInfo.restaurant.location.localityVerbose
            Glide.with(context).load(restaurantInfo.restaurant.thumb)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(iv_rest_thumb)
            tv_avg_rating.text = restaurantInfo.restaurant.userRating.aggregateRating
            tv_avg_rating.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor(restaurantInfo.restaurant.userRating.hexColor)
            )
            tv_avg_cost_two.text = String.format(
                Locale.ENGLISH, context.getString(R.string.avg_cost_for_two),
                restaurantInfo.restaurant.getAvgCostWithCurrency
            )

            cl_main_item_layout.setOnClickListener { listener(restaurantInfo.restaurant.url) }
        }
    }


    class NearbyRestaurantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}