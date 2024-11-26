package com.example.storyapp.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.FileProvider
import com.example.storyapp.BuildConfig
import com.google.android.material.floatingactionbutton.FloatingActionButton
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context) : Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context) : Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createRequestBodyFromFile(file: File) : MultipartBody.Part {
    val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData("photo", file.name, requestBody)
}

fun createRequestBodyFromText(text: String) : RequestBody {
    return text.toRequestBody("text/plain".toMediaTypeOrNull())
}



fun uriToFile(context: Context, uri: Uri): File {

    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File.createTempFile(timestamp, ".jpg", context.externalCacheDir)
    val contentResolver = context.contentResolver
    val inputStream: InputStream = contentResolver.openInputStream(uri) as InputStream
    val outputStream = FileOutputStream(file)

    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }

    inputStream.close()
    outputStream.close()

    return file
}

suspend fun compressFile(context: Context, file: File): File {
    val resolution = getIMGSize(file.path)
    val compressedFile = Compressor.compress(context, file) {
        resolution(resolution.first, resolution.second)
        quality(80)
        format(Bitmap.CompressFormat.JPEG)
        size(1_048_576)
    }

    return compressedFile
}

private fun getIMGSize(file: String): Pair<Int, Int> {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(File(file).absolutePath, options)
    val imageHeight = options.outHeight
    val imageWidth = options.outWidth

    return Pair(imageWidth, imageHeight)
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 8
}

fun isLoading(button: Button, progressBar: ProgressBar){
    button.visibility = View.INVISIBLE
    progressBar.visibility = View.VISIBLE
}

fun isNotLoading(button: Button, progressBar: ProgressBar) {
    progressBar.visibility = View.GONE
    button.visibility = View.VISIBLE
}

fun isLoadingRv(recyclerView: View, frameLayout: FrameLayout, fab1: FloatingActionButton, fab2: FloatingActionButton){
    recyclerView.visibility = View.INVISIBLE
    fab1.visibility = View.INVISIBLE
    fab2.visibility = View.INVISIBLE
    frameLayout.visibility = View.VISIBLE
}