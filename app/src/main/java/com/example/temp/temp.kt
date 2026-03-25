package com.example.temp

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

@Composable
fun temp(modifier: Modifier = Modifier,
         onHomeClick: () -> Unit = {},
 ){
    TopBar(onHomeClick = onHomeClick)
    Spacer(modifier = Modifier.height(10.dp))

    // File shit
    val context = LocalContext.current

    writeToFile(context, "playlist.txt", "All the small tjins >_<")
    println(readFromFile(context, "playlist.txt"))
}

fun writingToFile(context: Context, fileName: String, content: String) {
    try {
        // Use Context.openFileOutput for internal storage
        val fileOutputStream: FileOutputStream =
            context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fileOutputStream.write(content.toByteArray())
        fileOutputStream.close()
        // Optionally show a Toast or update UI state on success
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
    }
}

fun readingFromFile(context: Context, fileName: String): String? {
    val stringBuilder = StringBuilder()
    try {
        val fileInputStream: FileInputStream = context.openFileInput(fileName)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String? = bufferedReader.readLine()

        while (line != null) {
            stringBuilder.append(line)
            line = bufferedReader.readLine()
        }
        fileInputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception, return null or empty string
    }
    return stringBuilder.toString()
}