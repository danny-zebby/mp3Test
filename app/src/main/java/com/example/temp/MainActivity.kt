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
import androidx.compose.ui.platform.LocalContext
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

                pOP.playlistOfPlaylist.add(
                    Playlist(
                        id = 0,
                        name = "All Podcast",
                        mp3s = SnapshotStateList<MP3>()
                    )
                )
                pOP.playlistOfPlaylist.add(
                    Playlist(
                        id = 1,
                        name = "All Trash",
                        mp3s = SnapshotStateList<MP3>()
                    )
                )
                pOP.playlistOfPlaylist.add(
                    Playlist(
                        id = 2,
                        name = "All Songs",
                        mp3s = SnapshotStateList<MP3>()
                    )
                )

                // File shit
                // General text SHIT
                val context = LocalContext.current
                val file = "playlist.txt"
                writeToFile(context, file, "POG: All the small tjins >_<")
                var words = readFromFile(context, file)
                println(words)
                writeToFile(context, file, "POG: blinking so many times")
                words += readFromFile(context, file)
                println(words)

                // label shit
                val punkLabel = Label(Color.Red, "PUNKASS")
                val punkDTO = punkLabel.toDTO()
                words = punkDTO.name + "<>" + punkDTO.color
                writeToFile(context, file,words)
                var newWords = readFromFile(context, file)?.split("<>")
                newWords?.forEach { word->
                    println("POG: " + word)
                }
                val remasteredLabel = LabelDTO(
                    name = newWords!![0],
                    color = newWords[1])
                println("POG: Name: " + remasteredLabel.name + " Color(" + remasteredLabel.color)
                pOP.playlistOfPlaylist[2].setLabels(listOf(remasteredLabel.toLabel()))


                // MP3 testing
                val blink = MP3(30, "I miss you", "/storage/emulated/0/Download/")
                val blinkLabels = listOf<Label>(Label(Color.Blue, "WHINNYASSBITCH") , remasteredLabel.toLabel())
                blink.setLabels(blinkLabels)
                val oneDTO = blink.toDTO()
                words = "{" + oneDTO.id.toString() + "}{" + oneDTO.title + "}{" + oneDTO.path + "}{"
                oneDTO.labels.forEach{ label ->
                    words = words + "[<" + label.name + "><" + label.color + ">]"
                }
                words = words + "}"
                writeToFile(context, file,words)
                val text = readFromFile(context, file)
                println("POG: " + text)
                val mp3words = "\\{(.*?)\\}".toRegex()
                    .findAll(text)
                    .map{ it.groupValues[1] }
                    .toList()
                mp3words.forEach { word ->
                    println("POG: " + word)
                }
                var id = mp3words[0].toInt()
                var title = mp3words[1]
                var path = mp3words[2]
                var labelBlocks = "\\[(.*?)\\]".toRegex()
                    .findAll(mp3words[3])
                    .map { it.groupValues[1] }
                    .toList()
                var labels = labelBlocks.map { block ->
                    val parts = "\\<(.*?)\\>".toRegex()
                        .findAll(block)
                        .map { it.groupValues[1] }
                        .toList()
                    LabelDTO(name = parts[0], color = parts[1])
                }
                val mp3 = MP3DTO(id, title, path, labels).toMP3()
                pOP.playlistOfPlaylist[0].mp3s.add(mp3)


                // Playlist stuff
                val newPlaylist = Playlist(id = 3, name = "Blinking 182 times")
                val label = labels.map { lab ->
                    lab.toLabel()
                }
                newPlaylist.setLabels(label)
                pOP.playlistOfPlaylist.add(newPlaylist)
                pOP.playlistOfPlaylist[3].mp3s.add(mp3)
                pOP.playlistOfPlaylist[3].mp3s.add(MP3(31,"ALL THE SMALL THINGS!!","over here!!"))
                pOP.playlistOfPlaylist[3].mp3s.add(MP3(32,"First Date","here too"))
                val tempAll = pOP.playlistOfPlaylist[3].toDTO()
                var tempText = "{" + tempAll.id.toString() + "}{" + tempAll.name + "}{"
                tempAll.labels.forEach { label ->
                    tempText = tempText + "[<" + label.name + "><" + label.color + ">]"
                }
                tempText = tempText + "}{"
                tempAll.mp3s.forEach { song ->
                    tempText = tempText + "[<" + song.id + "><" + song.title + "><" +
                            song.path +">]"
                }
                tempText = tempText + "}"
                writeToFile(context, file,tempText)
                val newText = readFromFile(context, file)
                println("POG: " + text)
                val tempWords = "\\{(.*?)\\}".toRegex()
                    .findAll(newText)
                    .map{ it.groupValues[1] }
                    .toList()
                tempWords.forEach { word ->
                    println("POG: " + word)
                }
                id = tempWords[0].toInt()
                title = tempWords[1]
                path = tempWords[2]
                labelBlocks = "\\[(.*?)\\]".toRegex()
                    .findAll(mp3words[3])
                    .map { it.groupValues[1] }
                    .toList()
                labels = labelBlocks.map { block ->
                    val parts = "\\<(.*?)\\>".toRegex()
                        .findAll(block)
                        .map { it.groupValues[1] }
                        .toList()
                    LabelDTO(name = parts[0], color = parts[1])
                }

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
                            onPlaylistClick = { playlist ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },

                            onPodcastClick = { currentScreen = "pod" },

                            onTempClick = { currentScreen = "temp"},

                            // Checks if playlist name is not already taken, if so adds playlist to pOP
                            onAddPlaylist = { name, labels ->
                                if (pOP.playlistOfPlaylist.none { it.name == name }) {
                                    val newPlaylist = Playlist(id = pOP.nextPlaylistId, name = name)
                                    newPlaylist.setLabels(labels)
                                    pOP.playlistOfPlaylist.add(newPlaylist)
                                    pOP.nextPlaylistId++
//                                    savePlaylists(this, pOP.playlistOfPlaylist)
                                } else {
                                    // some code it's like hey this name already exist try again
                                }
                            },

                            // Deleted Playlist
                            onDeletePlaylist = { playlistId ->
                                pOP.playlistOfPlaylist.removeAll { it.id == playlistId }
//                                savePlaylists(this, pOP.playlistOfPlaylist)
                            },

                            // Go Home ? (I want to make an animation if you click home and are already home)
                            onHomeClick = { currentScreen = "home" },
                        )

                        "playlist" -> {
                            val playlist =
                                pOP.playlistOfPlaylist.find { it.id == selectedPlaylistId }
                            playlist?.let { currentPlaylist ->
                                PlaylistPage(
                                    modifier = Modifier.padding(innerPadding),

                                    playlist = currentPlaylist,

                                    onAddSong = { mp3 ->
                                        if (currentPlaylist.mp3s.none { it.id == mp3.id }) {
                                            currentPlaylist.mp3s.add(mp3)
//                                            savePlaylists(this, pOP.playlistOfPlaylist)
                                        }
                                    },

                                    onRemoveSong = { mp3 ->
                                        currentPlaylist.mp3s.removeAll { it.id == mp3.id }
//                                        savePlaylists(this, pOP.playlistOfPlaylist)
                                    },

                                    onEditPlaylist = { name, labels ->
                                        currentPlaylist.name = name
                                        currentPlaylist.setLabels(labels)
//                                        savePlaylists(this, pOP.playlistOfPlaylist)
                                    },

                                    onDeletePlaylist = { playlistId ->
                                        pOP.playlistOfPlaylist.removeAll { it.id == playlistId }
//                                        savePlaylists(this, pOP.playlistOfPlaylist)
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
                                allSongs = pOP.playlistOfPlaylist.first { it.id == 2 }!!,
                                allPodcast = pOP.playlistOfPlaylist.first { it.id == 0 }!!,
                                allTrash = pOP.playlistOfPlaylist.first { it.id == 1 }!!,

                                // Manage files
                                // Save example
                                onAddSong = { mp3 ->
                                    val playlist = pOP.playlistOfPlaylist.first { it.id == 2 }
                                    playlist.mp3s.add(mp3)
                                    allMP3s.remove(mp3)
//                                    savePlaylists(this, pOP.playlistOfPlaylist)
                                },
                                onAddTrash = { mp3 ->
                                    pOP.playlistOfPlaylist[1].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
//                                    savePlaylists(this, pOP.playlistOfPlaylist)
                                },
                                onAddPod = { mp3 ->
                                    pOP.playlistOfPlaylist[0].mp3s.add(mp3)
                                    allMP3s.remove(mp3)
//                                    savePlaylists(this, pOP.playlistOfPlaylist)
                                },
                            )
                        }

                        "pod" -> {
                            PodcastPage(
                                modifier = Modifier.padding(innerPadding),
                                // Go Home
                                onHomeClick = { currentScreen = "home" },
                                onRemovePod = { pod ->
                                    pOP.playlistOfPlaylist[0].mp3s.removeAll { it.id == pod.id }
                                    allMP3s.add(pod)
//                                    savePlaylists(this, pOP.playlistOfPlaylist)
                                },
                                onPodLabels = { pod, labels ->
                                    pOP.playlistOfPlaylist[0].mp3s[pOP.playlistOfPlaylist[0].mp3s.indexOf(
                                        pod
                                    )].setLabels(labels)
//                                    savePlaylists(this, pOP.playlistOfPlaylist)

                                }
                            )
                        }
                        "temp" ->{
                            temp(
                                modifier = Modifier.padding(innerPadding),
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
).also { mp3 ->
    mp3.setLabels(labels.map { it.toLabel() }) }


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


/*
class PlaylistRepo(private val context: Context){
    private val fileName = "playlists.json"

    fun save(playlists: List<Playlist>) {
        try {
            val dtoList = playlists.map { it.toDTO() }
            println("DTO READY")
            val json = Json.encodeToString<List<PlaylistDTO>>(dtoList)
            println("JSON CREATED: $json")
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            println("FILE WRITTEN")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun load(): List<Playlist> {
        return try {
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            println("LOADED JSON: $json")
            Json.decodeFromString<List<PlaylistDTO>>(json).map { it.toPlaylist() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}*/

/*
fun savePlaylists(context: Context, playlists: List<Playlist>) {
    try {
        val playlistsJson = mutableListOf<Map<String, Any>>()

        for (playlist in playlists) {
            val mp3List = mutableListOf<Map<String, Any>>()

            for (mp3 in playlist.mp3s) {
                val labelList = mp3.labels.map { mapOf("name" to it.name, "color" to it.color.value) }
                mp3List.add(mapOf(
                    "id" to mp3.id,
                    "title" to mp3.title,
                    "path" to mp3.path,
                    "labels" to labelList
                ))
            }

            val playlistLabels = playlist.labels.map { mapOf("name" to it.name, "color" to it.color.value) }

            playlistsJson.add(mapOf(
                "id" to playlist.id,
                "name" to playlist.name,
                "labels" to playlistLabels,
                "mp3s" to mp3List
            ))
        }

        val jsonString = Json.encodeToString(playlistsJson)
        context.openFileOutput("playlists.json", Context.MODE_PRIVATE).use { it.write(jsonString.toByteArray()) }

        println("SAVE DONE. JSON: $jsonString")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadPlaylists(context: Context): List<Playlist> {
    return try {
        val file = File(context.filesDir, "playlists.json")
        if (!file.exists()) return emptyList()

        val jsonString = file.readText()
        println("LOADED JSON: $jsonString")

        val playlistsList = Json.decodeFromString<List<Map<String, Any>>>(jsonString)
        val result = mutableListOf<Playlist>()

        for (plMap in playlistsList) {
            val playlist = Playlist(
                id = (plMap["id"] as Double).toInt(),
                name = plMap["name"] as String
            )

            val plLabels = plMap["labels"] as List<Map<String, Any>>
            playlist.labels.addAll(plLabels.map {
                Label(
                    name = it["name"] as String,
                    color = Color((it["color"] as Double).toLong())
                )
            })

            val mp3List = plMap["mp3s"] as List<Map<String, Any>>
            for (mp3Map in mp3List) {
                val mp3 = MP3(
                    id = (mp3Map["id"] as Double).toInt(),
                    title = mp3Map["title"] as String,
                    path = mp3Map["path"] as String
                )
                val mp3Labels = mp3Map["labels"] as List<Map<String, Any>>
                mp3.labels.addAll(mp3Labels.map {
                    Label(
                        name = it["name"] as String,
                        color = Color((it["color"] as Double).toLong())
                    )
                })
                playlist.mp3s.add(mp3)
            }

            result.add(playlist)
        }

        result
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
 */