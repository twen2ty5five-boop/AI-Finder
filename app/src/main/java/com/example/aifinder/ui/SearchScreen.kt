package com.example.aifinder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aifinder.data.ImageEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val images by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Gallery Finder") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.padding(16.dp)
            )
            PhotoGrid(images = images)
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search (e.g. Dog, Food)") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun PhotoGrid(images: List<ImageEntity>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(images) { image ->
            PhotoItem(image)
        }
    }
}

@Composable
fun PhotoItem(image: ImageEntity) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = image.uri,
            contentDescription = image.keywords,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
