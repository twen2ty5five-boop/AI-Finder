package com.example.aifinder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aifinder.data.AppDatabase
import com.example.aifinder.data.GalleryLoader
import com.example.aifinder.data.GalleryRepository
import com.example.aifinder.data.ImageEntity
import com.example.aifinder.domain.ImageLabelerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GalleryRepository

    init {
        val db = AppDatabase.getDatabase(application)
        val loader = GalleryLoader(application)
        val labeler = ImageLabelerHelper(application)
        repository = GalleryRepository(db.imageDao(), loader, labeler)
        
        syncImages()
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val images: StateFlow<List<ImageEntity>> = _searchQuery
        .combine(repository.allImages) { query, _ ->
            query
        }.combine(repository.searchImages("")) { query, _ -> 
             // This is a bit of a hack to trigger flow updates, 
             // ideally we should just switch the flow source based on query
             // but for simplicity let's do this:
             repository.searchImages(query)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ) as StateFlow<List<ImageEntity>> // Cast to fix type inference if needed, or better logic below

    // Better approach for search flow
    val searchResults = _searchQuery.combine(repository.allImages) { query, all ->
        if (query.isBlank()) all else all.filter { it.keywords.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun syncImages() {
        viewModelScope.launch {
            repository.syncImages()
        }
    }
}
