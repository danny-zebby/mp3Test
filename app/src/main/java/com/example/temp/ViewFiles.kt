package com.example.temp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.primaryBGLight
import com.example.compose.secondaryBGLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFiles(
    onAddSong: (MP3) -> Unit,
    onAddTrash: (MP3) -> Unit,
    onAddPod: (MP3) -> Unit,
    onHomeClick: () -> Unit = {},
    allMP3s: SnapshotStateList<MP3>,
    allSongs: Playlist,
    allPodcast: Playlist,
    allTrash:  Playlist,
){
    Column(modifier = Modifier.fillMaxSize().background(primaryBGLight))
    {
        Box(modifier = Modifier.height(50.dp) .fillMaxWidth() .background(secondaryBGLight)) { }
        TopBar(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "File Viewer",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Unassigned Files: (" + allMP3s.size + ")",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleLarge
        )
        // Var & vals for SearchBar
        var slectedMP3 by remember {mutableStateOf(-1)}
        var searchText by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(true) }
        // Recompute filtered songs whenever user types or adds a song
            // I need to rewrite this logic to have it only show for how I want it to
        val filteredSongs = remember(searchText, allSongs.mp3s) {
            allMP3s.filter { song ->
                song.title.contains(searchText, ignoreCase = true) &&
                        ( allSongs.mp3s.none { it.id == song.id } )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp) .height(250.dp)) {
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = {},
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Search songs") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.clickable { searchText = "" }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn {
                    items(filteredSongs, key = {it.id}) { song ->
                        Text(
                            text = song.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // I need to change this so it grabs the song only
                                     slectedMP3 = song.id
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {onAddSong(allMP3s.get(slectedMP3))}
            ) {
                Text("Add Songs\n("+allSongs.mp3s.size+")")
            }
            Button(
                onClick = {onAddTrash(allMP3s.get(slectedMP3))}
            ) {
                Text("Trash\n("+allSongs.mp3s.size+")")
            }
            Button(
                onClick = {onAddPod(allMP3s.get(slectedMP3))}
            ) {
                Text("Add Pod\n("+allPodcast.mp3s.size+")")
            }
        }

        BottomButtons()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewFilesPreview(){
    ViewFiles(
        allMP3s = SnapshotStateList<MP3>(),

        allSongs = Playlist(
        id = 0,
        name = "All Songs",
        labels = emptyList(),
        ),
        allPodcast = Playlist(
            id = 1,
            name = "All Pods",
            labels = emptyList(),
        ),
        allTrash = Playlist(
            id = 2,
            name = "All Trash",
            labels = emptyList(),
        ),
        onAddSong = {},
        onAddTrash = {},
        onAddPod = {}
    )
}