package com.example.zomato.restaurantListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zomato.data.RestaurantSearch
import com.example.zomato.databinding.ResListCardBinding

class RestaurantPagingAdapter(private val restaurantItemClicked: restaurantItemClicked) :
    PagingDataAdapter<RestaurantSearch, RestaurantPagingAdapter.viewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            ResListCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val res = getItem(position)
        holder.bind(res!!)
    }

    inner class viewHolder(private val binding: ResListCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                getItem(absoluteAdapterPosition)?.let { it1 ->
                    restaurantItemClicked.restItemClicked(
                        it1
                    )
                }
            }
        }

        fun bind(nearbyRestaurant: RestaurantSearch) {
            binding.restaurant = nearbyRestaurant
        }
    }

    class DiffCall : DiffUtil.ItemCallback<RestaurantSearch>() {
        override fun areItemsTheSame(
            oldItem: RestaurantSearch,
            newItem: RestaurantSearch
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: RestaurantSearch,
            newItem: RestaurantSearch
        ): Boolean {
            return oldItem == newItem
        }
    }
}

interface restaurantItemClicked {
    fun restItemClicked(restaurantSearch: RestaurantSearch)
}
