package com.example.zomato.NearbyPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zomato.databinding.TopCuisineCardBinding

class CuisineAdapter(private val clickHandlers: clickHandlers) :
    ListAdapter<String, CuisineAdapter.viewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            TopCuisineCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class viewHolder(private val binding: TopCuisineCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                clickHandlers.cuisineClicked(getItem(pos))
            }

        }

        fun bind(string: String) {
            binding.cuisine = string
            binding.colorval = absoluteAdapterPosition
        }
    }

    class DiffCall : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem)
        }
    }
}
