package com.example.myapplication.data

import android.net.Uri
import com.example.myapplication.domain.ImageLabelerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GalleryRepository(
    private val imageDao: ImageDao,
    private val galleryLoader: GalleryLoader,
    private val imageLabelerHelper: ImageLabelerHelper
) {
    val allImages: Flow<List<ImageEntity>> = imageDao.getAllImages()

    fun searchImages(query: String): Flow<List<ImageEntity>> {
        return if (query.isBlank()) {
            imageDao.getAllImages()
        } else {
            imageDao.searchImages(query)
        }
    }

    suspend fun syncImages() = withContext(Dispatchers.IO) {
        val mediaStoreImages = galleryLoader.getAllImages()
        val dbUris = imageDao.getAllUris().toSet()

        // 1. Find new images
        val newImages = mediaStoreImages.filter { it.uri.toString() !in dbUris }
        
        // 2. Process new images
        newImages.forEach { mediaImage ->
            val labels = imageLabelerHelper.getLabels(mediaImage.uri)
            val entity = ImageEntity(
                uri = mediaImage.uri.toString(),
                keywords = labels.joinToString(","),
                timestamp = mediaImage.dateModified
            )
            imageDao.insert(entity)
        }

        // 3. (Optional) Cleanup deleted images
        val currentMediaUris = mediaStoreImages.map { it.uri.toString() }.toSet()
        dbUris.forEach { dbUri ->
            if (dbUri !in currentMediaUris) {
                imageDao.deleteByUri(dbUri)
            }
        }
    }
}
