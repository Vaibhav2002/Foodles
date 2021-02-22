package com.example.zomato.SearchPage

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.zomato.MainActivity
import com.example.zomato.MainViewModel
import com.example.zomato.R
import com.example.zomato.data.RestaurantSearch
import com.example.zomato.databinding.FragmentSearchBinding
import com.example.zomato.restaurantListPage.ResLoadStateAdapter
import com.example.zomato.restaurantListPage.RestaurantPagingAdapter
import com.example.zomato.restaurantListPage.restaurantItemClicked
import com.example.zomato.util.SearchParameter
import com.google.android.gms.ads.InterstitialAd
import kotlinx.coroutines.flow.collect


class SearchFragment : Fragment(R.layout.fragment_search), restaurantItemClicked {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var interstitialAd: InterstitialAd
    private var boot: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boot = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        val searchAdapter = RestaurantPagingAdapter(this)
        interstitialAd = (activity as MainActivity).interstitialAd
        binding.searchRecycler.apply {
            adapter = searchAdapter.withLoadStateHeaderAndFooter(
                header = ResLoadStateAdapter { searchAdapter.retry() },
                footer = ResLoadStateAdapter { searchAdapter.retry() }
            )
            setHasFixedSize(true)
        }
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    viewModel.query.postValue(Pair(query, SearchParameter.QUERY))
                } else {
                    searchAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                }
                val imm =
                    context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                boot = true
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.searchRecycler.isVisible = false
                    searchAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    binding.noResultFound.isVisible = false
                }
                return true
            }
        })

        viewModel.searchByPaging.observe(viewLifecycleOwner, { response ->
            searchAdapter.submitData(viewLifecycleOwner.lifecycle, response)
        })

        searchAdapter.addLoadStateListener { loadState ->
            binding.apply {
                searchRecycler.isVisible = loadState.source.refresh is LoadState.NotLoading && boot
                loadingAnim.isVisible = loadState.source.refresh is LoadState.Loading && boot
                binding.noResultFound.isVisible =
                    (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached
                            && searchAdapter.itemCount < 1)
                binding.somethingWentWrong.isVisible = loadState.refresh is LoadState.Error
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { events ->
                if (events is MainViewModel.Events.ShowRestaurantDetail) {
                    if (interstitialAd.isLoaded)
                        interstitialAd.show()
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToRestaurantDetailFragment(
                            events.restaurantXSearch
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