import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.toInt
import androidx.core.content.getSystemService
import androidx.wear.compose.foundation.weight

object AudioPlayer {
    var mediaPlayer: android.media.MediaPlayer? = null
    // Use a State so Compose can observe changes
    var isPlaying by androidx.compose.runtime.mutableStateOf(false)

    fun playSong(context: Context, resId: Int) {
        // ... your loading logic ...
        mediaPlayer?.start()
        isPlaying = true // This will trigger the LaunchedEffect in HomeProfilePart
    }

    fun stopSong() {
        mediaPlayer?.stop()
        isPlaying = false
    }
}

// Inside Surface in HomeProfilePart
val context = androidx.compose.ui.platform.LocalContext.current
val audioManager = androidx.compose.runtime.remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

var sliderPosition by androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }

// This effect triggers IMMEDIATELY when AudioPlayer.isPlaying changes
LaunchedEffect(AudioPlayer.isPlaying) {
    if (AudioPlayer.isPlaying) {
        // MODE: Song Progress
        while (AudioPlayer.isPlaying) {
            val player = AudioPlayer.mediaPlayer
            if (player != null && player.duration > 0) {
                sliderPosition = player.currentPosition.toFloat() / player.duration
            }
            kotlinx.coroutines.delay(200) // Update every 200ms
        }
    } else {
        // MODE: Volume
        val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        sliderPosition = currentVol.toFloat() / maxVolume
    }
}



Row {
    if (AudioPlayer.isPlaying) { // Use the state here too
        val player = AudioPlayer.mediaPlayer
        // Use sliderPosition to calculate time for smoother UI if seeking
        val duration = player?.duration ?: 0
        val currentPos = (sliderPosition * duration).toInt()

        androidx.compose.material3.Text(text = "   ${formatTime(currentPos)}")
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        androidx.compose.material3.Text(text = "${formatTime(duration)}   ")
    } else {
        androidx.compose.material3.Text(text = "   Volume: ${(sliderPosition * 100).toInt()}%")
    }
}
