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
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.example.compose.primaryBGLight
import kotlinx.serialization.Serializable
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader


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
                // These values set the Top and Bottom of phone colors to match
                val window = this.window
                val statColor = Color(0xFF0191B3)
                val navColor = Color(0xFF196D8A)
                SideEffect {
                    window.statusBarColor = statColor.toArgb()
                    window.navigationBarColor = navColor.toArgb()
                }

                // Loads the playlist
                val context = LocalContext.current
//                pOP.clearFile(context)                // Commented out to work, but when I want to restart the app to see from a fresh user perspective I use this
                if(pOP.checkFile(context)) pOP.creation()
                else pOP.loadPlaylists(context)

                // These are vars that help perform playlist functions
                var currentScreen by remember { mutableStateOf("home") }
                var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = statColor
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> MP3Home(
                            modifier = Modifier.padding(innerPadding).fillMaxSize().background(primaryBGLight),

                            onSimpleModeClick = { currentScreen = "simple" },

                            onViewFilesClick = { currentScreen = "view" },

                            // Goes to playlist page when clicking on a playlist
                            onPlaylistClick = { playlist ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },

                            onPodcastClick = { currentScreen = "pod" },

                            onTempClick = { currentScreen = "temp"},

                            // Checks if playlist name is not already taken, if so adds playlist to pOP
                            onAddPlaylist = { name, labels ->
                                if (pOP.playlistOfPlaylist.none { it.name == name }) {
                                    val newPlaylist = Playlist(id = pOP.playlistIdCounter, name = name)
                                    newPlaylist.setLabels(labels)
                                    pOP.playlistOfPlaylist.add(newPlaylist)
                                    pOP.playlistIdCounter++
                                    pOP.savePlaylists(context)
                                } else {
                                    // some code it's like hey this name already exist try again
                                }
                            },

                            // Deleted Playlist
                            onDeletePlaylist = { playlistId ->
                                pOP.playlistOfPlaylist.removeAll { it.id == playlistId }
                                pOP.savePlaylists(context)
                            },

                            // Go Home ? (I want to make an animation if you click home and are already home)
                            onHomeClick = { currentScreen = "home" },
                        )

                        "playlist" -> {
                            val playlist =
                                pOP.playlistOfPlaylist.find { it.id == selectedPlaylistId }
                            playlist?.let { currentPlaylist ->
                                PlaylistPage(
                                    modifier = Modifier.padding(innerPadding).fillMaxSize().background(primaryBGLight),

                                    playlist = currentPlaylist,


                                    onAddSong = { mp3 ->
                                        if (currentPlaylist.mp3s.none { it.id == mp3.id }) {    // checks if song is not already in playlist then adds
                                            currentPlaylist.mp3s.add(mp3)
                                            pOP.savePlaylists(context)
                                        }
                                    },

                                    onRemoveSong = { mp3 ->
                                        currentPlaylist.mp3s.removeAll { it.id == mp3.id }
                                        pOP.savePlaylists(context)
                                    },

                                    onEditPlaylist = { name, labels ->
                                        currentPlaylist.name = name
                                        currentPlaylist.setLabels(labels)
                                        pOP.savePlaylists(context)
                                    },

                                    onDeletePlaylist = { playlistId ->
                                        pOP.playlistOfPlaylist.removeAll { it.id == playlistId }
                                        pOP.savePlaylists(context)
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

                                modifier = Modifier.padding(innerPadding).fillMaxSize().background(primaryBGLight)
                            )
                        }

                        "view" -> {
                            ViewFiles(
                                modifier = Modifier.padding(innerPadding).fillMaxSize().background(primaryBGLight),
                                // Go Home
                                onHomeClick = { currentScreen = "home" },

                                // Load the main four file list
                                allMP3s = pOP.playlistOfPlaylist.first { it.id == 2 },
                                allSongs = pOP.playlistOfPlaylist.first { it.id == 3 },
                                allPodcast = pOP.playlistOfPlaylist.first { it.id == 0 },
                                allTrash = pOP.playlistOfPlaylist.first { it.id == 1 },

                                // Manage files
                                // Save example
                                onAddSong = { mp3 ->
                                    pOP.playlistOfPlaylist[3].mp3s.add(mp3)
                                    pOP.playlistOfPlaylist[2].mp3s.remove(mp3)
                                    pOP.savePlaylists(context)
                                },
                                onAddTrash = { mp3 ->
                                    pOP.playlistOfPlaylist[1].mp3s.add(mp3)
                                    pOP.playlistOfPlaylist[2].mp3s.remove(mp3)
                                    pOP.savePlaylists(context)
                                },
                                onAddPod = { mp3 ->
                                    pOP.playlistOfPlaylist[0].mp3s.add(mp3)
                                    pOP.playlistOfPlaylist[2].mp3s.remove(mp3)
                                    pOP.savePlaylists(context)
                                },
                            )
                        }

                        "pod" -> {
                            PodcastPage(
                                modifier = Modifier.padding(innerPadding).fillMaxSize().background(primaryBGLight),
                                // Go Home
                                onHomeClick = { currentScreen = "home" },
                                onRemovePod = { pod ->
                                    pOP.playlistOfPlaylist[0].mp3s.removeAll { it.id == pod.id }
                                    pOP.playlistOfPlaylist[2].mp3s.add(pod)
                                    pOP.savePlaylists(context)
                                },
                                onPodLabels = { pod, labels ->
                                    pOP.playlistOfPlaylist[0].mp3s[pOP.playlistOfPlaylist[0].mp3s.indexOf(
                                        pod)].setLabels(labels)
                                    pOP.savePlaylists(context)
                                }
                            )
                        }
                        "temp" ->{
                            temp(
                                modifier = Modifier.padding(innerPadding).fillMaxSize().background(primaryBGLight),
                                // Go Home
                                onHomeClick = { currentScreen = "home" },
                            )
                        }
                    }
                }
            }
        }
    }
}


// Each value has a DTO counterpart and a DTO -> OG and vis versa
// Labels, for genres of playlist and podcast
data class Label(
    val color: Color,
    val name: String,
)

@Serializable
data class LabelDTO(
    val color: String,
    val name: String
)

fun Label.toDTO() = LabelDTO(
    color = color.value.toString(),
    name = name
)

fun LabelDTO.toLabel() = Label(
    color = Color(color.toULong()),
    name = name
)

// MP3s for songs and pods
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
    val labels: List<LabelDTO> = emptyList()
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
).also { mp3 ->
    mp3.setLabels(labels.map { it.toLabel() }) }


// Playlist collection of songs, pods, or just audio files
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
        name = name).also{ playlist ->
        playlist.setLabels(labels.map { it.toLabel() })
        playlist.mp3s.addAll(mp3s.map { it.toMP3() })
    }
}


// Singleton pOP so I can use the same one from all files easily (playlistOfPlaylist)
object pOP{
    //
    val playlistOfPlaylist = mutableStateListOf<Playlist>()
    val file = "PlaylistOfPlaylist.txt"
    var playlistIdCounter: Int = 3
    var mp3IdCounter: Int = 0

    // Not used b/c it's to restart my data to act as new user when testing
    fun clearFile(context: Context){
        writeToFile(context,file,"")
    }

    // Checks if a song exist in the POP via the mp3 path
    fun checkSong(path: String): Boolean {
        playlistOfPlaylist.forEach { playlist ->
            playlist.mp3s.forEach { audio ->
                if(audio.path == path) return true
            }
        }
        return false
    }

    // Checks if the file is empty, so we can know to load new user data or old data
    fun checkFile(context: Context): Boolean {
        val file = File(context.filesDir, file)
        return !file.exists() || file.length() == 0L
    }

    // Starter function that creates the new user data
    fun creation() {
        playlistOfPlaylist.add(Playlist(id = 0, name = "All Podcast", mp3s = SnapshotStateList<MP3>()))
        playlistOfPlaylist.add(Playlist(id = 1, name = "All Trash", mp3s = SnapshotStateList<MP3>()))
        playlistOfPlaylist.add(Playlist(id = 2, name = "Unassigned Files", mp3s = loadDownloadMP3s()))
        playlistOfPlaylist.add(Playlist(id = 3, name = "All Songs", mp3s = SnapshotStateList<MP3>()))
        playlistIdCounter = 4
    }

    // Collects all MP3 files from downloaded section of folders
    fun loadDownloadMP3s(): SnapshotStateList<MP3> {
        val list = mutableStateListOf<MP3>()
        val folder = File("/storage/emulated/0/Download/")

        folder.listFiles()?.forEach { file ->
            if (file.extension.lowercase() == "mp3") {
                if(!checkSong(file.absolutePath))
                    list.add(
                        MP3(
                            id = mp3IdCounter++,
                            title = file.nameWithoutExtension,
                            path = file.absolutePath
                        )
                    )
            }
        }
        return list
    }
    // Saves all data from POP to file

    fun savePlaylists(context: Context) {
        var text = ""
        playlistOfPlaylist.forEach { playlist ->
            val tempPlaylist = playlist.toDTO()
            text = text + "^{" + tempPlaylist.id.toString() + "}{" + tempPlaylist.name + "}{"
            tempPlaylist.labels.forEach { label ->
                text = text + "[<" + label.name + "><" + label.color + ">]"
            }
            text = text + "}{"
            tempPlaylist.mp3s.forEach { song ->
                text = text + "[<" + song.id + "><" + song.title + "><" +
                        song.path + ">]"
            }
            text = text + "}^"
        }
        writeToFile(context, file,text)
    }

    // Loads all data from file to POP
    fun loadPlaylists(context: Context) {
        val texting = readFromFile(context, file)
        val newText = "\\^(.*?)\\^".toRegex()
            .findAll(texting)
            .map{ it.groupValues[1] }
            .toList()
        newText.forEach { newText ->
            val tempWords = "\\{(.*?)\\}".toRegex()
                .findAll(newText)
                .map{ it.groupValues[1] }
                .toList()
            val id = tempWords[0].toInt()
            val title = tempWords[1]
            val labelBlocks = "\\[(.*?)\\]".toRegex()
                .findAll(tempWords[2])
                .map { it.groupValues[1] }
                .toList()
            val labels = labelBlocks.map { block ->
                val parts = "\\<(.*?)\\>".toRegex()
                    .findAll(block)
                    .map { it.groupValues[1] }
                    .toList()
                LabelDTO(name = parts[0], color = parts[1])
            }
            val mp3Block = "\\[(.*?)\\]".toRegex()
                .findAll(tempWords[3])
                .map { it.groupValues[1] }
                .toList()
            val theMP3s = mp3Block.map { block ->
                val parts = "\\<(.*?)\\>".toRegex()
                    .findAll(block)
                    .map { it.groupValues[1] }
                    .toList()
                MP3DTO(id = parts[0].toInt(), title = parts[1], path = parts[2])
            }
            val remasteredPlaylist = PlaylistDTO(id = id, name = title, labels = labels, mp3s = theMP3s)
            val rePlay = remasteredPlaylist.toPlaylist()
            playlistOfPlaylist.add(rePlay)
        }
        playlistIdCounter = (playlistOfPlaylist.maxOfOrNull { it.id } ?: -1) + 1
    }
}

// Singleton AudioPlayer so audio can be controlled from any page
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

    // Pauses (Unneeded Comment but Unneeded is an interesting spelling for a word)
    fun pause() {
        mediaPlayer?.pause()
    }

    // Resumes
    fun resume() {
        mediaPlayer?.start()
    }

    // Empties the media player
    fun onDispose() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Checks if media player is playing
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    // Restarts current audio playing
    fun replay () {
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
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

// Save
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

// Load
fun readFromFile(context: Context, fileName: String): String {
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