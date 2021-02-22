package com.example.zomato.restaurantListPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.zomato.MainActivity
import com.example.zomato.MainViewModel
import com.example.zomato.data.RestaurantSearch
import com.example.zomato.databinding.FragmentRestaurantListingBinding
import com.example.zomato.util.SearchParameter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.coroutines.flow.collect

class RestaurantListingFragment : Fragment(),
    restaurantItemClicked {
    private lateinit var binding: FragmentRestaurantListingBinding
    private lateinit var searchParameter: SearchParameter
    private lateinit var id: String
    private lateinit var viewModel: MainViewModel
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = RestaurantListingFragmentArgs.fromBundle(requireArguments())
        searchParameter = args.searchBy
        id = args.id
        viewModel = (activity as MainActivity).viewModel
        viewModel.query.postValue(Pair(id, searchParameter))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val resAdapter = RestaurantPagingAdapter(this)
        binding.resListRecycle.apply {
            adapter = resAdapter.withLoadStateHeaderAndFooter(
                header = ResLoadStateAdapter { resAdapter.retry() },
                footer = ResLoadStateAdapter { resAdapter.retry() }
            )
            setHasFixedSize(false)
            interstitialAd = (activity as MainActivity).interstitialAd
        }

        viewModel.searchByPaging.observe(viewLifecycleOwner, {
            resAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
        resAdapter.addLoadStateListener { loadState ->
            binding.loadingAnim.isVisible = loadState.source.refresh is LoadState.Loading
            binding.resListRecycle.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.noResultFound.isVisible =
                (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached
                        && resAdapter.itemCount < 1)
            binding.somethingWentWrong.isVisible = loadState.refresh is LoadState.Error

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                if (event is MainViewModel.Events.ShowRestaurantDetail) {
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                    } else {
                        interstitialAd.loadAd(AdRequest.Builder().build())
                        interstitialAd.show()
                    }
                    val action =
                        RestaurantListingFragmentDirections.actionRestaurantListingFragmentToRestaurantDetailFragment(
                            restaurant = event.restaurantXSearch
                        )
                    findNavController().navigate(action)
                }

            }
        }


    }


    override fun restItemClicked(restaurantSearch: RestaurantSearch) {
        viewModel.restaurantClicked(restaurantSearch.restaurant)
    }
}