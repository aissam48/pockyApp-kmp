package com.world.pockyapp.screens

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


@Composable
actual fun ImagePicker() {
    val context = LocalContext.current
    var onImagePickedCallback: ((String?) -> Unit)? = null
    fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }

    val pickImageLauncher = (context as ComponentActivity).registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val imagePath = uri?.let { getPathFromUri(context, it) }
        onImagePickedCallback?.invoke(imagePath)
    }

    fun pickI