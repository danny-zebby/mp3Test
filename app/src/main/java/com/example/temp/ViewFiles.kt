package com.example.temp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.primaryBGLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFiles(
    onHomeClick: () -> Unit = {}
){
    Column(modifier = Modifier.fillMaxSize().background(primaryBGLight)) {
        TopBar(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "File Viewer",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Unassigned Files: (" + "" + ")",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleLarge
        )
        // Var & vals for SearchBar
        var searchText by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(true) }
        // Recompute filtered songs whenever user types or adds a song
            // Replace allSongs -> AllFiles, playlist -> allSongs, allPodcast
        val filteredSongs = remember(searchText, playlist.songs) {
            allSongs.songs.filter { song ->
                song.title.contains(searchText, ignoreCase = true) &&
                        playlist.songs.none { it.id == song.id }
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
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
                                    onAddSong(song)
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewFilesPreview(){
    ViewFiles()
}