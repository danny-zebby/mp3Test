package com.example.temp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.secondaryBCLight
import com.example.compose.secondaryBGLight
import com.example.compose.tertiaryBGLight
import com.example.temp.ui.theme.NewTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay

fun formatTime(ms: Int?): String {

    if (ms == null || ms <= 0) return "0:00"

    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)
}

@Composable
fun HomeProfilePart(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // First row: Home button, Music Playing, and Profile page
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(secondaryBGLight)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Button
        Button(
            onClick = onHomeClick,
            modifier = Modifier.size(60.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(R.drawable.home),
                contentDescription = "Home",
                modifier = Modifier.size(60.dp)
            )
        }
        // Music Playing
        Surface(
            shape = RoundedCornerShape(25.dp),
            color = secondaryBCLight,
            modifier = Modifier.weight(1f).height(75.dp)
        ) {
            // Get the Audio Manager
            // Get the Audio Manager
            val context = LocalContext.current
            val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            // Initialize slider position
            var sliderPosition by remember { mutableFloatStateOf(0f) }

            // This effect runs whenever the "Playing" state changes
            LaunchedEffect(AudioPlayer.isPlaying()) {
                if (AudioPlayer.isPlaying()) {
                    // MUSIC MODE: Update slider position every 200ms
                    while (AudioPlayer.isPlaying()) {
                        val player = AudioPlayer.mediaPlayer
                        val duration = player?.duration ?: 1
                        val position = player?.currentPosition ?: 0
                        sliderPosition = position.toFloat() / duration
                        delay(200)
                    }
                } else {
                    // VOLUME MODE: Set slider to current system volume
                    val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    sliderPosition = currentVol.toFloat() / maxVolume
                }
            }
            // Listen for physical button presses
            DisposableEffect(context) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {

                        if (!AudioPlayer.isPlaying()) {

                            val newVolume =
                                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                            sliderPosition = newVolume.toFloat() / maxVolume
                        }
                    }
                }

                val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
                context.registerReceiver(receiver, filter)

                onDispose {
                    context.unregisterReceiver(receiver)
                }
            }
            Column {
                Row {
                    Slider(
                        value = sliderPosition,

                        onValueChange = { newValue ->

                            sliderPosition = newValue

                            if (AudioPlayer.isPlaying()) {

                                val player = AudioPlayer.mediaPlayer
                                val duration = player?.duration ?: 0

                                val seekPosition =
                                    (newValue * duration).toInt()

                                player?.seekTo(seekPosition)

                            } else {

                                val newVolume =
                                    (newValue * maxVolume).toInt()

                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    newVolume,
                                    0
                                )
                            }
                        },

                        valueRange = 0f..1f,

                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
                Row{
                    if (AudioPlayer.isPlaying()) {

                        val player = AudioPlayer.mediaPlayer
                        val position = player?.currentPosition ?: 0
                        val duration = player?.duration ?: 1

                        Text(
                            text = "   " + formatTime(position),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = formatTime(duration) + "   ",
                        )

                    } else {

                        Text(
                            text = "   ${(sliderPosition * 100).toInt()}"
                        )
                    }
                }
            }

        }

        // Profile Button
        /*
        Stats of songs played
        Stats of time listening to (etc)
        Show device storage
        */
        OutlinedButton(
            onClick = onProfileClick,
            modifier = Modifier.size(60.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(tertiaryBGLight),
            border = BorderStroke(2.dp, Color.Black),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Profile",
                modifier = Modifier.padding(4.dp)
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 3.dp,
        color = Color.Black
    )
}

//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeProfilePartPreview() {
    NewTheme {
        HomeProfilePart()
    }
}
