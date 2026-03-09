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
import com.example.temp.ui.theme.NewTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                
                val AllSongs = mutableStateListOf(
                    Song(1,"Creep"), Song(2,"Candy"), Song(3,"Amber"),
                    Song(4, "311"), Song(5, "Tu Falta De Querer")
                )
                val playlistOfPlaylist = mutableStateListOf(
                    Playlist(id = 1, name = "All Songs", songs = AllSongs)
                )
                var nextPlaylistId by remember { mutableStateOf(2) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = statColor
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> MP3Home(
                            playlistOfPlaylist = playlistOfPlaylist,

                            onDriveModeClick = { currentScreen = "driving" },

                            onPlaylistClick = { playlist ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },

                            onAddPlaylist = { name, labels ->
                                if(playlistOfPlaylist.none {it.name == name}){
                                    playlistOfPlaylist.add(Playlist(id = nextPlaylistId, name = name, labels = labels))
                                    nextPlaylistId++
                                }
                                else{
                                    // some code it's like hey this name already exist try again
                                }
                            },

                            onDeletePlaylist = { index ->
                                if(playlistOfPlaylist.any {playlistOfPlaylist[index].id == it.id}){
                                    playlistOfPlaylist.remove(playlistOfPlaylist[index])
                                    nextPlaylistId--
                                }else{
                                    // some code it's like hey this play doesn't exist try again
                                }
                            },

                            onHomeClick = { currentScreen = "home" },

                            modifier = Modifier.padding(innerPadding)
                        )
                        "playlist" -> {
                            val playlist = playlistOfPlaylist.find { it.id == selectedPlaylistId }
                            playlist?.let { currentPlaylist ->
                                PlaylistPage(

                                    allSongs = playlistOfPlaylist.first { it.id == 1 },

                                    playlist = currentPlaylist,

                                    onAddSong = { song ->
                                        if (currentPlaylist.songs.none { it.id == song.id }) {
                                            currentPlaylist.songs.add(song)
                                        }
                                    },

                                    onRemoveSong = { song ->
                                        if (currentPlaylist.songs.any {it.id == song.id}){
                                            currentPlaylist.songs.remove(song)
                                        }
                                    },

                                    onHomeClick = { currentScreen = "home" },

                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                        "driving" -> {
                            DrivingMode(
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
    val id: Int,
    val title: String,
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

