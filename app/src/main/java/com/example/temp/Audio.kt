package com.example.temp

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.temp.ui.theme.NewTheme

@Composable
fun AudioPlayerScreen(onHomeClick: () -> Unit, modifier: Modifier = Modifier) {

    val filePath = "/storage/emulated/0/Download/Candy.mp3"

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HomeProfilePart(onHomeClick = onHomeClick)

        Button(
            onClick = {
                try {

                    if (mediaPlayer == null) {
                        // First time play
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(filePath)
                            prepare()
                            start()
                        }

                    } else {
                        // Resume if paused
                        if (!mediaPlayer!!.isPlaying) {
                            mediaPlayer?.start()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("PLAYER_ERROR", e.toString())
                }
            }
        ) {
            Text("Play / Resume")
        }

        Button(
            onClick = {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                }
            }
        ) {
            Text("Pause")
        }

        BottomButtons()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AudioPreview() {
    NewTheme {
        AudioPlayerScreen(onHomeClick = {})
    }
}