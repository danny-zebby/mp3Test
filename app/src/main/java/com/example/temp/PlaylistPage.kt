package com.example.temp


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.primaryContainerLight

@Composable
fun PlaylistPage(modifier: Modifier = Modifier) {
Column(
    modifier = modifier
        .fillMaxSize()
        .background(primaryContainerLight)
) {
    HomeProfilePart()
    Spacer(modifier = Modifier.height(10.dp))

    Spacer(modifier = Modifier.weight(1f))
    BottomButtons()
    }

}


//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlaylistPagePreview() {
    AppTheme {
        PlaylistPage()
    }
}