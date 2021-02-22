package com.example.zomato.NearbyPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.zomato.MainActivity
import com.example.zomato.MainViewModel
import com.example.zomato.data.Category
import com.example.zomato.data.NearbyRestaurant
import com.example.zomato.databinding.FragmentNearbyBinding
import com.example.zomato.util.COLOR_CONSTANT
import com.example.zomato.util.NO_INTERNET
import com.example.zomato.util.Resource
import com.example.zomato.util.SearchParameter
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

interface clickHandlers {
    fun categoryClicked(category: Category)
    fun cuisineClicked(cuisine: String)
}

@AndroidEntryPoint
class NearbyFragment : Fragment(), clickHandlers, restaurantClicked {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentNearbyBinding
    private val categoryAdapter = CategoryAdapter(this)
    private val cuisineAdapter = CuisineAdapter(this)
    private val restAdapter = RestaurantAdapter(this)
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNearbyBinding.inflate(inflater, container, false)
        (activity as MainActivity).supportActionBar?.show()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
        viewModel = (activity as MainActivity).viewModel
        COLOR_CONSTANT = 0
        interstitialAd = (activity as MainActivity).interstitialAd
        //setting up recycler views
        binding.categoryRecycler.apply {
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
        binding.topCuisineRecycle.apply {
            adapter = cuisineAdapter
            setHasFixedSize(true)
        }
        binding.nearbyRestaurant.apply {
            adapter = restAdapter
            setHasFixedSize(false)

        }
        if (!viewModel.checkInternet()) {
            showViewForNoInternet()
        }
        binding.swipe.setOnRefreshListener {
            if (viewModel.checkInternet() && viewModel.location.value != null) {
                binding.swipe.isRefreshing = true
                viewModel.refetchDataForNearbyPage()
                binding.swipe.isRefreshing = false
            } else if (!viewModel.checkInternet()) {
                showViewForNoInternet()
                binding.swipe.isRefreshing = false
            } else if (viewModel.location.value == null) {
                (activity as MainActivity).showViewsForLocationError()
                binding.swipe.isRefreshing = false
            }
        }

        viewModel.categories.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    categoryAdapter.submitList(it.data?.categories)
                }
                is Resource.Error -> {
                    if (it.message == NO_INTERNET)
                        viewModel.internetError()
                    else showViewsForError()
                }
                else -> Timber.d("Don't know what happened")
            }
        })
        viewModel.nearby.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    showViewForLoading()
                }
                is Resource.Success -> {
                    showViewsForSuccess()
                    cuisineAdapter.submitList(it.data?.popularity?.top_cuisines)
                    restAdapter.submitList(it.data?.nearby_restaurants?.toMutableList())
                }
                is Resource.Error -> {
                    if (it.message == NO_INTERNET)
                        viewModel.internetError()
                    else
                        showViewsForError()
                }
                else -> Timber.d("Don't know what happened")
            }
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is MainViewModel.Events.ListByCategory -> {
                        val action =
                            NearbyFragmentDirections.actionNearbyFragmentToRestaurantListingFragment(
                                event.category.categories.id.toString(),
                                SearchParameter.CATEGORY
                            )
                        findNavController().navigate(action)
                    }
                    is MainViewModel.Events.ListByCuisine -> {
                        val action =
                            NearbyFragmentDirections.actionNearbyFragmentToRestaurantListingFragment(
                                event.cuisine,
                                SearchParameter.CUISINE
                            )
                        findNavController().navigate(action)
                    }
                    is MainViewModel.Events.ShowRestaurantDetail -> {
                        if (interstitialAd.isLoaded)
                            interstitialAd.show()
                        val action =
                            NearbyFragmentDirections.actionNearbyFragmentToRestaurantDetailFragment(
                                event.restaurantXSearch
                            )
                        findNavController().navigate(action)
                    }
                    is MainViewModel.Events.ShowInternetError -> {
                        showViewForNoInternet()
                    }
                }
            }
        }
        return binding.root
    }

    override fun restaurantClicked(restaurant: NearbyRestaurant) {
        viewModel.restaurantClicked(restaurant.restaurant)
    }

    override fun categoryClicked(category: Category) {
        viewModel.categoryCLicked(category)
    }

    override fun cuisineClicked(cuisine: String) {
        viewModel.cuisineClicked(cuisine)
    }

    private fun showViewsForSuccess() {
        (activity as MainActivity).showViewsForSuccess()
        binding.connectToInternet.isVisible = false
        binding.content.visibility = View.VISIBLE
        binding.somethingWentWrong.isVisible = false
        binding.loadingAnim.visibility = View.INVISIBLE
    }

    private fun showViewsForError() {
        binding.apply {
            (activity as MainActivity).showViewsForSuccess()
            connectToInternet.isVisible = false
            somethingWentWrong.isVisible = true
            content.isVisible = false
            loadingAnim.isVisible = false
        }
    }

    private fun showViewForNoInternet() {
        binding.apply {
            (activity as MainActivity).showViewsForSuccess()
            connectToInternet.isVisible = true
            content.isVisible = false
            somethingWentWrong.isVisible = false
            loadingAnim.isVisible = false
        }
    }

    private fun showViewForLoading() {
        binding.apply {
            (activity as MainActivity).showViewsForSuccess()
            connectToInternet.isVisible = false
            content.visibility = View.INVISIBLE
            somethingWentWrong.isVisible = false
            loadingAnim.visibility = View.VISIBLE
        }
    }


}