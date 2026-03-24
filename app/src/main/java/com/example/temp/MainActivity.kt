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
import java.io.File
import android.Manifest
import android.media.MediaPlayer
import androidx.core.app.ActivityCompat
import android.content.Context
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

        val repo = PlaylistRepo(this)
        val loaded = repo.load()
        pOP.playlistOfPlaylist.clear()
        if(loaded.isNotEmpty()){
            pOP.playlistOfPlaylist.addAll(loaded)
        }
        else{
            pOP.playlistOfPlaylist.add(Playlist(id = 0, name = "All Podcast", mp3s = SnapshotStateList<MP3>()))
            pOP.playlistOfPlaylist.add(Playlist(id = 1, name = "All Trash", mp3s = SnapshotStateList<MP3>()))
            pOP.playlistOfPlaylist.add(Playlist(id = 2, name = "All Songs", mp3s = SnapshotStateList<MP3>()))
        }

        setContent {
            NewTheme {
//                val context = LocalContext.current

                // These values set the Top and Bottom of phone colors to match
                val window = this.window
                val statColor = Color(0xFF0191B3)
                val navColor = Color(0xFF196D8A)
                SideEffect {
                    window.statusBarColor = statColor.toArgb()
                    window.navigationBarColor = navColor.toArgb()
                }

                // These are the main list
                val allMP3s = remember { loadDownloadMP3s() }

                // These are vars that help perform playlist functions
                var currentScreen by remember { mutableStateOf("home") }
                var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = statColor
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> MP3Home(
                            modifier = Modifier.padding(innerPadding),

                            onSimpleModeClick = { currentScreen = "simple" },

                            onViewFilesClick = { currentScreen = "view" },

                            // Goes to playlist page when clicking on a playlist
                            onPlaylistClick = { playlist  ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },

                            onPodcastClick = { currentScreen = "pod" },

                            // Checks if playlist name is not already taken, if so adds playlist to pOP
                            onAddPlaylist = { name, labels ->
                                if(pOP.playlistOfPlaylist.none {it.name == name}){
                                    pOP.playlistOfPlaylist.add(Playlist(
                                        id = pOP.nextPlaylistId, name = name))
                                    pOP.playlistOfPlaylist[pOP.nextPlaylistId].setLabels(labels)
                                    pOP.nextPlaylistId++
                                    repo.save(pOP.playlistOfPlaylist)
                                }
                                else{
                                    // some code it's like hey this name already exist try again
                                }
                            },

                            // Deleted Playlist
                            onDeletePlaylist = { playlistId ->
                                pOP.playlistOfPlaylist.removeAll { it.id == playlistId }
                                repo.save(pOP.playlistOfPlaylist)
                            },

                            // Go Home ? (I want to make an animation if you click home and are already home)
                            onHomeClick = { currentScreen = "home" },
                        )
                        "playlist" -> {
                            val playlist = pOP.playlistOfPlaylist.find { it.id == selectedPlaylistId }
                            playlist?.let { currentPlaylist ->
                                PlaylistPage(
                                    modifier = Modifier.padding(innerPadding),

                                    playlist = currentPlaylist,

                                    onAddSong = { mp3 ->
                                        if (currentPlaylist.mp3s.none { it.id == mp3.id }) {
                                            currentPlaylist.mp3s.add(mp3)
                                            repo.save(pOP.playlistOfPlaylist)
                                        }
                                    },

                                    onRemoveSong = { mp3 ->
                                        currentPlaylist.mp3s.removeAll{ it.id == mp3.id }
                                        repo.save(pOP.playlistOfPlaylist)
                                    },

                                    onEditPlaylist = {name, labels ->
                                        currentPlaylist.name = name
                                        currentPlaylist.setLabels(labels)
                                        repo.save(pOP.playlistOfPlaylist)
                                    },

                                    onDeletePlaylist = {playlistId ->
                                        pOP.playlistOfPlaylist.removeAll { it.id == playlistId }
                                        repo.save(pOP.playlistOfPlaylist)
                                    },

                                    // Go Home
                                    onHomeClick = { currentScreen = "home" },
                                )
                            }
                        }
                        "simple" -> {
                            SimpleMode(
                                // Go Home
                                onHomeClick = { currentScreen = "home" },

                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "view" -> {
                            ViewFiles(
                                modifier = Modifier.padding(innerPadding),
                                // Go Home
                                onHomeClick = { currentScreen = "home" },

                                // Load the main four file list
                                allMP3s = allMP3s,
                                allSongs = pOP.playlistOfPlaylist.first { it.id == 2 },
                                allPodcast = pOP.playlistOfPlaylist.first { it.id == 0 },
                                allTrash = pOP.playlistOfPlaylist.first { it.id == 1 },

                                // Manage files
                                onAddSong = { mp3 ->
                                    pOP.playlistOfPlaylist[2].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
                                    repo.save(pOP.playlistOfPlaylist)
                                },
                                onAddTrash = { mp3 ->
                                    pOP.playlistOfPlaylist[1].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
                                    repo.save(pOP.playlistOfPlaylist)
                                },
                                onAddPod = { mp3 ->
                                    pOP.playlistOfPlaylist[0].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
                                    repo.save(pOP.playlistOfPlaylist)
                                },
                            )
                        }
                        "pod" -> {
                            PodcastPage(
                                modifier = Modifier.padding(innerPadding),
                                // Go Home
                                onHomeClick = { currentScreen = "home" },
                                onRemovePod = { pod ->
                                    pOP.playlistOfPlaylist[0].mp3s.removeAll{ it.id == pod.id }
                                    allMP3s.add(pod)
                                    repo.save(pOP.playlistOfPlaylist)
                                },
                                onPodLabels ={ pod, labels ->
                                    pOP.playlistOfPlaylist[0].mp3s[pOP.playlistOfPlaylist[0].mp3s.indexOf(pod)].setLabels(labels)
                                    repo.save(pOP.playlistOfPlaylist)

                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Label(
    val color: Color,
    val name: String,
)

@Serializable
data class LabelDTO(
    val color: Long,
    val name: String
)

fun Label.toDTO() = LabelDTO(
    color = color.value.toLong(),
    name = name
)

fun LabelDTO.toLabel() = Label(
    color = Color(color),
    name = name
)

data class MP3(
    val id: Int = -1,
    val title: String = "",
    val path: String = "",
    val labels: SnapshotStateList<Label> = mutableStateListOf(),
) {
    var selected by mutableStateOf(false)
    fun setLabels(newLabels: List<Label>){
        labels.clear()
        labels.addAll(newLabels)
    }
}

@Serializable
data class MP3DTO(
    val id: Int,
    val title: String,
    val path: String,
    val labels: List<LabelDTO>
)

fun MP3.toDTO() = MP3DTO(
    id = id,
    title = title,
    path = path,
    labels = labels.map { it.toDTO() }
)

fun MP3DTO.toMP3() = MP3(
    id = id,
    title = title,
    path = path,
).apply { setLabels(this@toMP3.labels.map { it.toLabel() }) }


data class Playlist(
    val id: Int = -1,
    var name: String = "",
    val labels: SnapshotStateList<Label> = mutableStateListOf(),
    val mp3s: SnapshotStateList<MP3> = mutableStateListOf(),
){
    fun setLabels(newLabels: List<Label>){
        labels.clear()
        labels.addAll(newLabels)
    }
}

@Serializable
data class PlaylistDTO(
    val id: Int,
    var name: String,
    val labels: List<LabelDTO>,
    val mp3s: List<MP3DTO>,
)

fun Playlist.toDTO(): PlaylistDTO {
    return PlaylistDTO(
        id = id,
        name = name,
        labels = labels.map {it.toDTO()},
        mp3s = mp3s.map { it.toDTO() }
    )
}

fun PlaylistDTO.toPlaylist(): Playlist {
    return Playlist(
        id = id,
        name = name).apply{
            setLabels(this@toPlaylist.labels.map { it.toLabel() })
            mp3s.addAll(this@toPlaylist.mp3s.map { it.toMP3() })
    }
}


// Singleton pOP so I can use the same one from all files easily (playlistOfPlaylist)
object pOP{
    val playlistOfPlaylist = mutableStateListOf<Playlist>()
    var nextPlaylistId: Int = 3
}

// Singleton pOP so I can use the same one from all files easily
object AudioPlayer{
    var mediaPlayer: MediaPlayer? = null
    var currentSong by mutableStateOf(MP3())
    var currentPlaylist: Playlist = Playlist()
    var playQueue: List<MP3> = emptyList()
    var currentIndex: Int = -1
    var repeatPlaylist = true
    var repeatSong = false


    // Plays audio, used to jump straight into the selected audio
    fun play(song: MP3, playlist: Playlist, queue: List<MP3>) {
        // If audio is already playing do nothing
        if (currentSong == song) return
        // Else, stop playing audio and load up the next file
        onDispose()

        currentSong = song
        currentPlaylist = playlist
        playQueue = queue
        currentIndex = queue.indexOf(song)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.path)
            prepare()
            start()

            // Load next file for autoPlay
            setOnCompletionListener {
                nextSong()
            }
        }
    }

    // Restarts current audio playing
    fun replay () {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }

    // Empties the media player
    fun onDispose() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Pauses (Unneeded Comment but Unneeded is an interesting spelling for a word)
    fun pause() {
        mediaPlayer?.pause()
    }

    // Resumes
    fun resume() {
        mediaPlayer?.start()
    }

    // Checks if media player is playing
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    // Used to skip to next song or Autoplay or Loop
    //Still need to work on use cases for the following functions
    fun nextSong() {
        if(repeatSong) play(currentSong, currentPlaylist, playQueue)

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
        val currentIndex = pOP.playlistOfPlaylist.indexOf(currentPlaylist)
        if (currentIndex == pOP.playlistOfPlaylist.lastIndex) return
        val nextPlaylist = pOP.playlistOfPlaylist[currentIndex + 1]
        if (nextPlaylist.mp3s.isEmpty()) return
        play(nextPlaylist.mp3s[0], nextPlaylist, playQueue)
    }

    fun prevPlaylist() {
        val currentIndex = pOP.playlistOfPlaylist.indexOf(currentPlaylist)
        if (currentIndex == 0) return
        val nextPlaylist = pOP.playlistOfPlaylist[currentIndex - 1]
        if (nextPlaylist.mp3s.isEmpty()) return
        play(nextPlaylist.mp3s[0], nextPlaylist, playQueue)
    }

}

// Collects all MP3 files from downloaded section of folders
fun loadDownloadMP3s(): SnapshotStateList<MP3> {
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

class PlaylistRepo(private val context: Context){
    fun save(playlists: List<Playlist>){
        try{val dtoList = playlists.map { it.toDTO() }
            val json = Json.encodeToString(dtoList)

            context.openFileOutput("playlist.json", Context.MODE_PRIVATE).use { it.write(json.toByteArray()) }
        } catch (e: Exception) { e.printStackTrace() }
    }
    fun load(): List<Playlist> {
        return try {
            val json = context.openFileInput("playlists.json")
                .bufferedReader()
                .use { it.readText() }

            val dtoList = Json.decodeFromString<List<PlaylistDTO>>(json)
            dtoList.map { it.toPlaylist() }

        } catch (e: Exception) {
            emptyList()
        }
    }
}