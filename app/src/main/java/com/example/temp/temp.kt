package com.example.temp

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
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
    Column(modifier = modifier) {
        TopBar(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))

        Text("temp")

        Spacer(modifier = Modifier.weight(1f))
        BottomButtons()
    }
}