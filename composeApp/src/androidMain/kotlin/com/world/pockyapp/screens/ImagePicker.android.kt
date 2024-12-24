package com.world.pockyapp.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.world.pockyapp.navigation.NavRoutes


@Composable
actual fun ImagePicker(navController: NavHostController) {
    val context = LocalContext.current

    fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }

    // Activity Result Launcher for image picking
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            val imagePath = uri?.let { getPathFromUri(context, it) }

            println("imagePath -> $imagePath")
            val encodedFilePath = Uri.encode(imagePath)
            navController.navigate(NavRoutes.MOMENT_PREVIEW.route + "/${encodedFilePath}")

        }
    )




    Button(onClick = { pickImageLauncher.launch("image/*") }) {
        Text("Pick Image")
    }
}
