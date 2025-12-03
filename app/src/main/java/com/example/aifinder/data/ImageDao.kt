package com.example.aifinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE keywords LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchImages(query: String): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE uri = :uri LIMIT 1")
    suspend fun getImageByUri(uri: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity)

    @Query("DELETE FROM images WHERE uri = :uri")
    suspend fun deleteByUri(uri: String)
    
    @Query("SELECT uri FROM images")
    suspend fun getAllUris(): List<String>
}
