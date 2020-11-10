package com.codinginflow.imagesearchapp.ui.gallery

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.codinginflow.imagesearchapp.data.UnsplashRepository

class GalleryViewModel @ViewModelInject constructor(
    private val repository: UnsplashRepository,
    @Assisted private val savedState: SavedStateHandle
) : ViewModel() {

    companion object {
        const val DEFAULT_QUERY = "dogs"
        const val CURRENT_QUERY = "current_query"
    }

    private val currentQuery = savedState.getLiveData(CURRENT_QUERY , DEFAULT_QUERY)

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    val photos = currentQuery.switchMap { repository.getSearchResults(it).cachedIn(viewModelScope) }
}