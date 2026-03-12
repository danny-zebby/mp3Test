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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
            val context = LocalContext.current
            val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            // Initialize slider with the current system volume
            var sliderPosition by remember {
                mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume)
            }
            // Listen for physical button presses
            DisposableEffect(context) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        val newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                        sliderPosition = newVolume.toFloat() / maxVolume
                    }
                }
                // Register the receiver for volume changes
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
                            if(AudioManger.isPlaying()){
                                sliderPosition = newValue
                                val newVolume = (newValue * maxVolume).toInt()
                                // Update system volume (0 flag means hide the system volume bar)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
                            }else{
                                sliderPosition = newValue
                            }
                        },

                        // primaryBCLight & primaryBGLight
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.background,
                        )
                    )
                }
                Row{
                    Text(
                        text = "   " + String.format("%.2f", sliderPosition),
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    if(AudioManger.isPlaying()){
                        Text( text = String.format("%.2f", (1-sliderPosition)) + "   " )
                    }else{
                        Text( text = "")
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
