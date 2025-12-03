package com.example.myapplication.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.net.Uri

data class MediaStoreImage(
    val id: Long,
    val uri: Uri,
    val dateModified: Long
)

class GalleryLoader(private val context: Context) {
    fun getAllImages(): List<MediaStoreImage> {
        val imageList = mutableListOf<MediaStoreImage>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateModified = cursor.getLong(dateColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageList.add(MediaStoreImage(id, contentUri, dateModified))
            }
        }
        return imageList
    }
}
