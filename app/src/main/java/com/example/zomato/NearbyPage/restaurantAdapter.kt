package com.example.zomato.NearbyPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zomato.data.NearbyRestaurant
import com.example.zomato.databinding.RestaurantCardBinding

class RestaurantAdapter(private val restaurantClicked: restaurantClicked) :
    ListAdapter<NearbyRestaurant, RestaurantAdapter.viewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            RestaurantCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class viewHolder(private val binding: RestaurantCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                restaurantClicked.restaurantClicked(getItem(adapterPosition))
            }
        }

        fun bind(nearbyRestaurant: NearbyRestaurant) {
            binding.restaurant = nearbyRestaurant
        }
    }

    class DiffCall : DiffUtil.ItemCallback<NearbyRestaurant>() {
        override fun areItemsTheSame(
            oldItem: NearbyRestaurant,
            newItem: NearbyRestaurant
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: NearbyRestaurant,
            newItem: NearbyRestaurant
        ): Boolean {
            return oldItem == newItem
        }
    }

}

interface restaurantClicked {
    fun restaurantClicked(restaurant: NearbyRestaurant)
}
