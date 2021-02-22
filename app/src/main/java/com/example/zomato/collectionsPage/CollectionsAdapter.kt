package com.example.zomato.collectionsPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zomato.data.Collections
import com.example.zomato.databinding.CollectionsCardBinding

class CollectionsAdapter(private val onCollectionCLick: onCollectionCLick) :
    ListAdapter<Collections, CollectionsAdapter.viewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return viewHolder(CollectionsCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class viewHolder(private val binding: CollectionsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = adapterPosition
                onCollectionCLick.onCollectionCLick(getItem(pos))
            }
        }

        fun bind(collections: Collections) {
            binding.collection = collections
        }
    }

    class DiffCall : DiffUtil.ItemCallback<Collections>() {
        override fun areItemsTheSame(oldItem: Collections, newItem: Collections): Boolean {
            return oldItem.collection.collection_id == newItem.collection.collection_id
        }

        override fun areContentsTheSame(oldItem: Collections, newItem: Collections): Boolean {
            return oldItem == newItem
        }
    }

}

interface onCollectionCLick {
    fun onCollectionCLick(collections: Collections)
}