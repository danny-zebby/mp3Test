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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.primaryBGLight
import com.example.compose.secondaryBGLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFiles(
    modifier: Modifier = Modifier,
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
        // Var & vals for SearchBar
        var selectedMP3Index by remember {mutableIntStateOf(-1)}
        var searchText by remember { mutableStateOf("") }

        // Recompute filtered songs whenever user types or adds a song
        val filteredSongs by remember {
            derivedStateOf {
                allMP3s.filter { song ->
                    song.title.contains(searchText, ignoreCase = true) &&
                            allSongs.mp3s.none { it.id == song.id } &&
                            allPodcast.mp3s.none { it.id == song.id } &&
                            allTrash.mp3s.none { it.id == song.id }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp) .weight(1f) .fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Unassigned Files: (" + allMP3s.size + ")") },
                modifier = Modifier.fillMaxWidth()
            )
                LazyColumn {
                    items(filteredSongs, key = {it.id}) { song ->
                        val bg = if(song.selected) Color.DarkGray else Color.LightGray
                        Text(
                            text = song.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // When a song in LazyColumn is clicked:
                                    val clickedIndex = allMP3s.indexOf(song)

                                    if (clickedIndex != -1) {
                                        if (selectedMP3Index == clickedIndex) {
                                            // Clicked the already selected song -> deselect
                                            selectedMP3Index = -1
                                            song.selected = false
                                        } else {
                                            // Deselect previous selection if any
                                            if (selectedMP3Index != -1) {
                                                allMP3s[selectedMP3Index].selected = false
                                            }
                                            // Select new song
                                            selectedMP3Index = clickedIndex
                                            song.selected = true
                                        }
                                    } else {
                                        // Song not found in allMP3s (edge case)
                                        selectedMP3Index = -1
                                        song.selected = false
                                    }
                                }
                                .padding(top = 10.dp)
                                .background(bg)
                        )
                    }
                }
            }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (selectedMP3Index > -1){ onAddSong(allMP3s[selectedMP3Index])
                        selectedMP3Index = -1 } else {}
                }
            ) {
                Text("Add Songs\n("+allSongs.mp3s.size+")")
            }
            Button(
                onClick = {
                    if (selectedMP3Index > -1){ onAddTrash(allMP3s[selectedMP3Index])
                        selectedMP3Index = -1 } else {}
                }
            ) {
                Text("Trash\n("+allTrash.mp3s.size+")")
            }
            Button(
                onClick = {
                    if (selectedMP3Index > -1){ onAddPod(allMP3s[selectedMP3Index])
                        selectedMP3Index = -1 } else {}
                }
            ) {
                Text("Add Pod\n("+allPodcast.mp3s.size+")")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
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
        ),
        allPodcast = Playlist(
            id = 1,
            name = "All Pods",
        ),
        allTrash = Playlist(
            id = 2,
            name = "All Trash",
        ),
        onAddSong = {},
        onAddTrash = {},
        onAddPod = {}
    )
}