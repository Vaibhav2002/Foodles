package com.example.zomato.collectionsPage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.zomato.MainActivity
import com.example.zomato.MainViewModel
import com.example.zomato.R
import com.example.zomato.data.Collections
import com.example.zomato.databinding.FragmentCollectionsBinding
import com.example.zomato.util.NO_INTERNET
import com.example.zomato.util.Resource
import com.example.zomato.util.SearchParameter
import kotlinx.coroutines.flow.collect


class CollectionsFragment : Fragment(R.layout.fragment_collections), onCollectionCLick {
    private lateinit var binding: FragmentCollectionsBinding
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCollectionsBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        val callAdapter = CollectionsAdapter(this)
        viewModel.collect.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    showViewForLoading()
                }
                is Resource.Success -> {
                    showViewsForSuccess()
                    callAdapter.submitList(it.data?.collections?.toMutableList())
                }
                is Resource.Error -> {
                    if (it.message == NO_INTERNET)
                        showViewForNoInternet()
                    else showViewsForError()
                }
            }
        })
        binding.recycle.apply {
            adapter = callAdapter
            setHasFixedSize(true)
        }
        binding.swipe.setOnRefreshListener {
            if (viewModel.checkInternet() && viewModel.location.value != null) {
                binding.swipe.isRefreshing = true
                viewModel.refetchDataForCollectionPage()
                binding.swipe.isRefreshing = false
            } else if (!viewModel.checkInternet()) {
                showViewForNoInternet()
                binding.swipe.isRefreshing = false
            } else if (viewModel.location.value == null) {
                (activity as MainActivity).showViewsForLocationError()
                binding.swipe.isRefreshing = false
            }

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is MainViewModel.Events.ListByCollection -> {
                        val action = CollectionsFragmentDirections
                            .actionCollectionsFragmentToRestaurantListingFragment(
                                id = event.collections.collection.collection_id.toString(),
                                SearchParameter.COLLECTION
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onCollectionCLick(collections: Collections) {
        viewModel.collectionClicked(collections)
    }

    private fun showViewsForSuccess() {
        (activity as MainActivity).showViewsForSuccess()
        binding.connectToInternet.isVisible = false
        binding.content.visibility = View.VISIBLE
        binding.loadingAnim.visibility = View.INVISIBLE
    }

    private fun showViewForNoInternet() {
        binding.apply {
            (activity as MainActivity).showViewsForSuccess()
            connectToInternet.isVisible = true
            content.isVisible = false
            loadingAnim.isVisible = false
        }
    }

    private fun showViewForLoading() {
        binding.apply {
            (activity as MainActivity).showViewsForSuccess()
            connectToInternet.isVisible = false
            content.visibility = View.INVISIBLE
            loadingAnim.visibility = View.VISIBLE
        }
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
}