package com.example.aifinder.domain

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException

class ImageLabelerHelper(private val context: Context) {
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    suspend fun getLabels(uri: Uri): List<String> {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val labels = labeler.process(image).await()
            labels.take(5).map { it.text }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
