package com.example.temp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import com.example.temp.ui.theme.NewTheme
import java.io.File
import android.Manifest
import android.media.MediaPlayer
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request audio permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
            1
        )

        setContent {
            NewTheme {
                val window = this.window
                val statColor = Color(0xFF0191B3)
                val navColor = Color(0xFF196D8A)

                SideEffect {
                    window.statusBarColor = statColor.toArgb()
                    window.navigationBarColor = navColor.toArgb()
                }

                var currentScreen by remember { mutableStateOf("home") }
                var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }
                
                val allSongs = loadDownloadSongs()

                PoP.playlistOfPlaylist.add(Playlist(id = 0, name = "All Songs", songs = allSongs))

                var nextPlaylistId by remember { mutableIntStateOf(1) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = statColor
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> MP3Home(
                            modifier = Modifier.padding(innerPadding),

                            onAudioClick = {currentScreen = "audio"},

                            onSimpleModeClick = { currentScreen = "simple" },

                            onPlaylistClick = { playlist,  ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },

                            onAddPlaylist = { name, labels ->
                                if(PoP.playlistOfPlaylist.none {it.name == name}){
                                    PoP.playlistOfPlaylist.add(Playlist(id = nextPlaylistId, name = name, labels = labels))
                                    nextPlaylistId++
                                }
                                else{
                                    // some code it's like hey this name already exist try again
                                }
                            },

                            onDeletePlaylist = { playlistId ->
                                PoP.playlistOfPlaylist.removeAll { it.id == playlistId }
                            },

                            onHomeClick = { currentScreen = "home" },
                        )
                        "playlist" -> {
                            val playlist = PoP.playlistOfPlaylist.find { it.id == selectedPlaylistId }
                            playlist?.let { currentPlaylist ->
                                PlaylistPage(

                                    allSongs = PoP.playlistOfPlaylist.first { it.id == 0 },

                                    playlist = currentPlaylist,

                                    onAddSong = { song ->
                                        if (currentPlaylist.songs.none { it.id == song.id }) {
                                            currentPlaylist.songs.add(song)
                                        }
                                    },

                                    onRemoveSong = { song ->
                                        currentPlaylist.songs.removeAll{ it.id == song.id }
                                    },

                                    onHomeClick = { currentScreen = "home" },

                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                        "simple" -> {
                            SimpleMode(
                                onHomeClick = { currentScreen = "home" },

                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "audio" -> {
                            AudioPlayerScreen(
                                onHomeClick = { currentScreen = "home" },

                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Song(
    val id: Int = -1,
    val title: String = "",
    val path: String = "",
)

data class Label(
    val color: Color,
    val name: String,
)

data class Playlist(
    val id: Int = -1,
    val name: String = "",
    val labels: List<Label> = emptyList(), 
    val songs: SnapshotStateList<Song> = mutableStateListOf()
)
object AudioPlayer{
    var mediaPlayer: MediaPlayer? = null
    var currentSong: Song = Song()
    var currentPlaylist: Playlist = Playlist()

    fun play(song: Song, playlist: Playlist) {
        if(currentSong == song)
        {
            return
        }
        if(currentSong != song){
            onDispose()
        }
        currentSong = song
        currentPlaylist = playlist
        mediaPlayer = MediaPlayer().apply {
                setDataSource(song.path)
                prepare()
                start()
            }
    }

    fun replay () {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    fun onDispose() {
        mediaPlayer?.release()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    //Still need to work on use cases for the following functions
    fun nextSong() {
        if( currentPlaylist.songs.indexOf(currentSong) == currentPlaylist.songs.lastIndex ) {
            return
        }else {
            play(currentPlaylist.songs[currentPlaylist.songs.indexOf(currentSong)+1], currentPlaylist)
        }
    }

    fun prevSong() {
        if( currentPlaylist.songs.indexOf(currentSong) == 0 ) {
            return
        }else {
            play(currentPlaylist.songs[currentPlaylist.songs.indexOf(currentSong)-1], currentPlaylist)
        }
    }

    fun nextPlaylist() {
        if(PoP.playlistOfPlaylist.indexOf(currentPlaylist) == PoP.playlistOfPlaylist.lastIndex
            || PoP.playlistOfPlaylist[PoP.playlistOfPlaylist.indexOf(currentPlaylist) + 1 ].songs.isEmpty()){
            return
        }else {
            play(PoP.playlistOfPlaylist[PoP.playlistOfPlaylist.indexOf(currentPlaylist) + 1 ].songs[0], PoP.playlistOfPlaylist[PoP.playlistOfPlaylist.indexOf(currentPlaylist) + 1 ])
        }
    }

    fun prevPlaylist() {
        if (PoP.playlistOfPlaylist.indexOf(currentPlaylist) == 0
            || PoP.playlistOfPlaylist[PoP.playlistOfPlaylist.indexOf(currentPlaylist) - 1 ].songs.isEmpty()){
            return
        } else {
            play(PoP.playlistOfPlaylist[PoP.playlistOfPlaylist.indexOf(currentPlaylist) - 1].songs[0], PoP.playlistOfPlaylist[PoP.playlistOfPlaylist.indexOf(currentPlaylist) - 1])
        }
    }

}

object PoP{
    val playlistOfPlaylist: MutableList<Playlist> = mutableListOf()
}
fun loadDownloadSongs(): SnapshotStateList<Song> {

    val list = mutableStateListOf<Song>()

    val folder = File("/storage/emulated/0/Download/")

    var idCounter = 0

    folder.listFiles()?.forEach { file ->

        if (file.extension.lowercase() == "mp3") {

            list.add(
                Song(
                    id = idCounter++,
                    title = file.nameWithoutExtension,
                    path = file.absolutePath
                )
            )

        }
    }

    return list
}