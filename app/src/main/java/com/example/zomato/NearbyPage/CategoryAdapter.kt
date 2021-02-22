package com.example.zomato.NearbyPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zomato.data.Category
import com.example.zomato.databinding.CategoriesCardBinding

class CategoryAdapter(private val clickHandlers: clickHandlers) :
    ListAdapter<Category, CategoryAdapter.viewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            CategoriesCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class viewHolder(private val binding: CategoriesCardBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val pos = adapterPosition
                clickHandlers.categoryClicked(getItem(pos))
            }

        }

        fun bind(category: Category) {
            binding.category = category
        }
    }

    class DiffCall : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.categories.id == newItem.categories.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

}
