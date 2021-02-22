package com.example.zomato.restaurantDetailPage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zomato.R
import com.example.zomato.data.RestaurantXSearch
import com.example.zomato.databinding.FragmentRestaurantDetailBinding

class RestaurantDetailFragment : Fragment(R.layout.fragment_restaurant_detail) {
    private lateinit var binding: FragmentRestaurantDetailBinding
    private lateinit var restaurant: RestaurantXSearch
    private lateinit var chipAdapter: chipAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRestaurantDetailBinding.bind(view)
        val args = RestaurantDetailFragmentArgs.fromBundle(requireArguments())
        restaurant = args.restaurant
        binding.restaurant = restaurant
        chipAdapter = chipAdapter()
        binding.cuisinesRecyclerView.apply {
            adapter = chipAdapter
            setHasFixedSize(true)
        }
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        val list = restaurant.cuisines.split(",")
        chipAdapter.submitList(list)
        binding.orderFab.setOnClickListener {
            if (restaurant.url.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(restaurant.url)
                startActivity(intent)
            }
        }
    }
}