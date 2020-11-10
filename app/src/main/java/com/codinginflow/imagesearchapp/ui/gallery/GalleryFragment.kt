package com.codinginflow.imagesearchapp.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.data.UnsplashPhoto
import com.codinginflow.imagesearchapp.databinding.FrgmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frgment_gallery.*

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.frgment_gallery) , UnsplashPhotoAdapter.onItemClicklistener {

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FrgmentGalleryBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding = FrgmentGalleryBinding.bind(view)
        val adapter = UnsplashPhotoAdapter(this)
        binding.apply {
            recycleView.setHasFixedSize(true)
            recycleView.itemAnimator = null
            recycleView.adapter = adapter.withLoadStateHeaderAndFooter(
                unsplashLodingAdapter { adapter.retry() },
                unsplashLodingAdapter { adapter.retry() }
            )
            retry_button.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadstate ->
            binding.apply {
                progress_bar.isVisible = loadstate.source.refresh is LoadState.Loading
                recycle_view.isVisible = loadstate.source.refresh is LoadState.NotLoading
                retry_button.isVisible = loadstate.source.refresh is LoadState.Error
                text_view_error.isVisible = loadstate.source.refresh is LoadState.Error

                if(loadstate.source.refresh is LoadState.NotLoading &&
                        loadstate.append.endOfPaginationReached &&
                        adapter.itemCount == 0){
                    recycleView.isVisible = false
                    textViewEmpty.isVisible = true
                }else{
                    textViewEmpty.isVisible = false
                }

            }
        }


        setHasOptionsMenu(true)
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val  action = GalleryFragmentDirections.actionGalleryFragment2ToDetails(photo)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchPhotos(query!!)
                binding.recycleView.scrollToPosition(0)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}