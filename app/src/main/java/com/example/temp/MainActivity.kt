package com.example.temp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.concurrent.atomic.AtomicInteger

// --- DATA CLASSES ---

// Represents a single song
data class Song(
    val id: Int,       // Unique ID for Compose LazyColumn or state tracking
    val title: String, // Display name of the song
    val path: String   // Path or URI as string (for playback)
)

// Represents a label or tag for a playlist (optional)
data class Label(
    val color: Color,
    val name: String
)

// Represents a playlist of songs
data class Playlist(
    val id: Int = -1,
    val name: String = "",
    val labels: List<Label> = emptyList(),
    val songs: SnapshotStateList<Song> = mutableStateListOf() // Compose-friendly list
)

class MainActivity : ComponentActivity() {

    companion object {
        // Request code for file picker activity
        const val REQUEST_CODE_PICK_AUDIO = 1001
    }

    // Counter to give each Song a unique ID
    private val nextSongId = AtomicInteger(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // --- THEME AND SYSTEM COLORS ---
            val statColor = Color(0xFF0191B3) // Status bar color
            val navColor = Color(0xFF196D8A)  // Navigation bar color
            SideEffect {
                // Apply system bar colors (needs to be inside SideEffect)
                window.statusBarColor = statColor.toArgb()
                window.navigationBarColor = navColor.toArgb()
            }

            // --- APP STATE ---
            // Tracks which screen is currently visible
            var currentScreen by remember { mutableStateOf("home") }

            // Tracks which playlist is selected (for PlaylistPage)
            var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }

            // List of all playlists, including "All Songs"
            val playlistOfPlaylist = remember {
                mutableStateListOf(
                    Playlist(id = 1, name = "All Songs")
                )
            }

            // --- SCAFFOLD ---
            // Provides the basic layout structure with background color
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = statColor
            ) { innerPadding ->

                // --- SCREEN ROUTING ---
                when (currentScreen) {

                    // HOME SCREEN
                    "home" -> MP3Home(
                        modifier = Modifier.padding(innerPadding),
                        playlistOfPlaylist = playlistOfPlaylist,

                        // Callbacks for buttons
                        onAudioClick = { currentScreen = "audio" },
                        onDriveModeClick = { currentScreen = "driving" },
                        onPlaylistClick = { playlist ->
                            selectedPlaylistId = playlist.id
                            currentScreen = "playlist"
                        },

                        // Add a new playlist (name + labels)
                        onAddPlaylist = { name, labels ->
                            if (playlistOfPlaylist.none { it.name.equals(name, ignoreCase = true) }) {
                                val nextId = (playlistOfPlaylist.maxOfOrNull { it.id } ?: 1) + 1
                                playlistOfPlaylist.add(Playlist(id = nextId, name = name, labels = labels))
                            }
                        },

                        // Delete a playlist
                        onDeletePlaylist = { playlistId ->
                            playlistOfPlaylist.removeAll { it.id == playlistId }
                            if (selectedPlaylistId == playlistId) {
                                selectedPlaylistId = null
                                currentScreen = "home"
                            }
                        },

                        // Launch the system file picker to add songs
                        onAddSongsClick = { pickAudioFiles() },

                        // Return to home screen
                        onHomeClick = { currentScreen = "home" }
                    )

                    // PLAYLIST SCREEN
                    "playlist" -> {
                        val playlist = playlistOfPlaylist.find { it.id == selectedPlaylistId }
                        playlist?.let { currentPlaylist ->
                            PlaylistPage(
                                // Pass "All Songs" as reference for adding songs
                                allSongs = playlistOfPlaylist.first { it.id == 1 },
                                playlist = currentPlaylist,

                                // Add a song to this playlist
                                onAddSong = { song ->
                                    if (currentPlaylist.songs.none { it.id == song.id }) {
                                        currentPlaylist.songs.add(song)
                                    }
                                },

                                // Remove a song from this playlist
                                onRemoveSong = { song ->
                                    currentPlaylist.songs.removeAll { it.id == song.id }
                                },

                                onHomeClick = { currentScreen = "home" },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    // DRIVING MODE SCREEN
                    "driving" -> DrivingMode(
                        onHomeClick = { currentScreen = "home" },
                        modifier = Modifier.padding(innerPadding)
                    )

                    // AUDIO PLAYER SCREEN
                    "audio" -> AudioPlayerScreen(
                        onHomeClick = { currentScreen = "home" },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // --- FILE PICKER ---
    // Launches system picker to select MP3 files
    private fun pickAudioFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/mpeg"                  // Only MP3 files
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selection
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
    }

    // Handle result from file picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                val clipData = intent.clipData
                val uriList = mutableListOf<Uri>()

                // Handle multiple files selected
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        uriList.add(clipData.getItemAt(i).uri)
                    }
                } else {
                    // Single file selected
                    intent.data?.let { uriList.add(it) }
                }

                // Add selected files to "All Songs" playlist
                val allSongsPlaylist = (playlistOfPlaylist.firstOrNull { it.id == 1 }
                    ?: return)
                uriList.forEach { uri ->
                    val name = getFileName(uri)
                    val song = Song(
                        id = nextSongId.getAndIncrement(),
                        title = name,
                        path = uri.toString() // Use URI string for playback
                    )
                    allSongsPlaylist.songs.add(song) // Compose updates UI automatically
                }
            }
        }
    }

    // --- HELPER FUNCTION ---
    // Get display name of a file from its URI
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                result = cursor.getString(nameIndex)
            }
        }
        return result ?: "Unknown"
    }
}