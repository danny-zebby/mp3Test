package com.example.audiotest

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.temp.BottomButtons
import com.example.temp.HomeProfilePart
import com.example.temp.MP3Home
import com.example.temp.Playlist
import com.example.temp.ui.theme.NewTheme

@Composable
fun AudioPlayerScreen(onHomeClick: () -> Unit, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        audioUri = uri
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeProfilePart(onHomeClick = onHomeClick)

        Button(
            onClick = { picker.launch(arrayOf("audio/mpeg")) }
        ) {
            Text("Select Audio File")
        }

        Button(
            onClick = {
                audioUri?.let { uri ->

                    mediaPlayer?.release()

                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, uri)
                        prepare()
                        start()
                    }
                }
            }
        ) {
            Text("Play")
        }

        Button(
            onClick = {
                mediaPlayer?.pause()
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