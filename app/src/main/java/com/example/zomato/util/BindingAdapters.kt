package com.example.zomato.util

import android.os.Build
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.zomato.R
import com.example.zomato.data.Category
import com.example.zomato.data.Collections
import com.example.zomato.data.NearbyRestaurant
import com.example.zomato.data.RestaurantXSearch

@BindingAdapter("setCollectionImage")
fun ImageView.setCollectionImg(collections: Collections) {
    Glide.with(context).load(collections.collection.image_url)
        .error(R.drawable.ic_baseline_error_24)
        .into(this)
}


@BindingAdapter("setCategoryColor")
fun CardView.setCategoryColor(category: Category) {
    setCardBackgroundColor(context.getColor(colorsForCategory[category.categories.id - 1]))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        outlineAmbientShadowColor = context.getColor(colorsForCategory[category.categories.id - 1])
        outlineSpotShadowColor = context.getColor(colorsForCategory[category.categories.id - 1])
    }
}

@BindingAdapter("setCuisineColor")
fun CardView.setCuisineColor(colorVal: Int) {
    setCardBackgroundColor(context.getColor(colorsForCategory[colorVal]))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        outlineAmbientShadowColor = context.getColor(colorsForCategory[colorVal])
        outlineSpotShadowColor = context.getColor(colorsForCategory[colorVal])
    }
}

@BindingAdapter("setRestaurantImage")
fun ImageView.setRestaurantImage(nearbyRestaurant: NearbyRestaurant) {
    Glide.with(context).load(nearbyRestaurant.restaurant.thumb).into(this)
}

@BindingAdapter("setRestaurantStars")
fun RatingBar.setRestaurantStar(rating: String) {
    this.rating = rating.toFloat()
}

@BindingAdapter("setRestaurantSearchImage")
fun ImageView.setRestaurantSearchImage(url: String) {
    Glide.with(context).load(url).into(this)
}

@BindingAdapter("setTiming")
fun TextView.setTiming(restaurantXSearch: RestaurantXSearch) {
    text =
        if (restaurantXSearch.timings == null || restaurantXSearch.timings.isEmpty()) "Not Available" else restaurantXSearch.timings
}

@BindingAdapter("setMobileNo")
fun TextView.setMobileNo(restaurantXSearch: RestaurantXSearch) {
    text =
        if (restaurantXSearch.phone_numbers == null || restaurantXSearch.phone_numbers.isEmpty()) "Not Available" else restaurantXSearch.phone_numbers
}

@BindingAdapter("setAvgCost")
fun TextView.setAvgCost(restaurantXSearch: RestaurantXSearch) {
    text =
        if (restaurantXSearch.average_cost_for_two == null || restaurantXSearch.average_cost_for_two.toString()
                .isEmpty()
        ) "Not Available" else restaurantXSearch.average_cost_for_two.toString()
}
