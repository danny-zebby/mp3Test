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
import android.content.Context
import java.io.FileOutputStream
import java.io.IOException
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import androidx.compose.ui.platform.LocalContext

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
                val context = LocalContext.current

                val window = this.window
                val statColor = Color(0xFF0191B3)
                val navColor = Color(0xFF196D8A)

                SideEffect {
                    window.statusBarColor = statColor.toArgb()
                    window.navigationBarColor = navColor.toArgb()
                }

                var currentScreen by remember { mutableStateOf("home") }
                var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }
                
                val allMP3s = loadDownloadMP3s()
                val allSongs = SnapshotStateList<MP3>()
                val allPodcast = SnapshotStateList<MP3>()
                val allTrash =  SnapshotStateList<MP3>() // COME UP WITH BETTER NAME

                PoP.playlistOfPlaylist.add(Playlist(id = 0, name = "All Songs", mp3s = allSongs, type = PlaylistType.Song))
                PoP.playlistOfPlaylist.add(Playlist(id = 1, name = "All Podcast", mp3s = allPodcast, type = PlaylistType.Pod))
                PoP.playlistOfPlaylist.add(Playlist(id = 2, name = "All Trash", mp3s = allTrash,type = PlaylistType.Trash))

                var nextPlaylistId by remember { mutableIntStateOf(1) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = statColor
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> MP3Home(
                            modifier = Modifier.padding(innerPadding),

                            onSimpleModeClick = { currentScreen = "simple" },

                            onViewFilesClick = {currentScreen = "view"},

                            onPlaylistClick = { playlist,  ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },

                            onAddPlaylist = { name, type, labels ->
                                if(PoP.playlistOfPlaylist.none {it.name == name}){
                                    PoP.playlistOfPlaylist.add(Playlist(
                                        id = nextPlaylistId, name = name, labels = labels, type = type))
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

                                    onAddSong = { mp3 ->
                                        if (currentPlaylist.mp3s.none { it.id == mp3.id }) {
                                            currentPlaylist.mp3s.add(mp3)
                                        }
                                    },

                                    onRemoveSong = { mp3 ->
                                        currentPlaylist.mp3s.removeAll{ it.id == mp3.id }
                                    },

                                    onEditPlaylist = {name, label ->
                                        currentPlaylist.name = name
                                        currentPlaylist.labels = label
                                    },

                                    onDeletePlaylist = {playlistId ->
                                        PoP.playlistOfPlaylist.removeAll { it.id == playlistId }
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
                        "view" -> {
                            ViewFiles(
                                onHomeClick = { currentScreen = "home" },
                                allMP3s = allMP3s,
                                allSongs = PoP.playlistOfPlaylist.first { it.id == 0 },
                                allPodcast = PoP.playlistOfPlaylist.first { it.id == 1 },
                                allTrash = PoP.playlistOfPlaylist.first { it.id == 2 },
                                onAddSong = { mp3 ->
                                    PoP.playlistOfPlaylist[0].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
                                },
                                onAddTrash = { mp3 ->
                                    PoP.playlistOfPlaylist[2].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
                                },
                                onAddPod = { mp3 ->
                                    PoP.playlistOfPlaylist[1].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class PlaylistType{
    Pod,
    Song,
    Trash,
    Null,
}

data class MP3(
    val id: Int = -1,
    val title: String = "",
    val path: String = "",
    var selected: Boolean = false,
)

data class Label(
    val color: Color,
    val name: String,
)

data class Playlist(
    val id: Int = -1,
    var name: String = "",
    var labels: List<Label> = emptyList(),
    val mp3s: SnapshotStateList<MP3> = mutableStateListOf(),
    val type: PlaylistType = PlaylistType.Null,
)

object AudioPlayer{
    var mediaPlayer: MediaPlayer? = null
    var currentSong: MP3 = MP3()
    var currentPlaylist: Playlist = Playlist()
    var playQueue: List<MP3> = emptyList()
    var currentIndex: Int = -1
    var repeatPlaylist = true
    var repeatSong = false


    fun play(song: MP3, playlist: Playlist, queue: List<MP3>) {

        if (currentSong == song) return

        onDispose()

        currentSong = song
        currentPlaylist = playlist
        playQueue = queue
        currentIndex = queue.indexOf(song)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.path)
            prepare()
            start()

            setOnCompletionListener {
                nextSong()
            }
        }
    }

    fun replay () {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    fun onDispose() {
        mediaPlayer?.release()
        mediaPlayer = null
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
        if(repeatSong==true) play(currentSong, currentPlaylist, playQueue)

        if (currentIndex >= playQueue.lastIndex) {
            if (repeatPlaylist) play(playQueue.first(), currentPlaylist, playQueue)
            return
        }
        val next = playQueue[currentIndex + 1]
        play(next, currentPlaylist, playQueue)
    }

    fun prevSong() {
        if (currentIndex <= 0) return
        val prev = playQueue[currentIndex - 1]
        play(prev, currentPlaylist, playQueue)
    }

    fun nextPlaylist() {
        val currentIndex = PoP.playlistOfPlaylist.indexOf(currentPlaylist)
        if (currentIndex == PoP.playlistOfPlaylist.lastIndex) return
        val nextPlaylist = PoP.playlistOfPlaylist[currentIndex + 1]
        if (nextPlaylist.mp3s.isEmpty()) return
        play(nextPlaylist.mp3s[0], nextPlaylist, playQueue)
    }

    fun prevPlaylist() {
        val currentIndex = PoP.playlistOfPlaylist.indexOf(currentPlaylist)
        if (currentIndex == 0) return
        val nextPlaylist = PoP.playlistOfPlaylist[currentIndex - 1]
        if (nextPlaylist.mp3s.isEmpty()) return
        play(nextPlaylist.mp3s[0], nextPlaylist, playQueue)
    }

}

object PoP{
    val playlistOfPlaylist = mutableStateListOf<Playlist>()
}
fun loadDownloadMP3s(): SnapshotStateList<MP3> {
    //
    val list = mutableStateListOf<MP3>()
    val folder = File("/storage/emulated/0/Download/")
    var idCounter = 0

    folder.listFiles()?.forEach { file ->
        if (file.extension.lowercase() == "mp3") {
            list.add(
                MP3(
                    id = idCounter++,
                    title = file.nameWithoutExtension,
                    path = file.absolutePath
                )
            )
        }
    }
    //
    return list
}

fun writeToFile(context: Context, fileName: String, content: String) {
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

fun readFromFile(context: Context, fileName: String): String? {
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