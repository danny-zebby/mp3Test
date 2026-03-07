package com.example.temp

import BottomButtons
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.compose.onPrimaryLight
import com.example.compose.primaryContainerLight
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPage(
    allSongs: Playlist,
    playlist: Playlist,
    onAddSong: (Song) -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Values use to create playlist
    val playlistOfSongs = remember { mutableStateListOf<Song>().apply { addAll(playlist.songs) } }
    var addSong by remember { mutableStateOf(false) }

    // Values used to create three playlist sorts
    var sortIndex by remember { mutableStateOf(0) }                         // 0: Custom, 1: Alpha
    var isAlphaAsc by remember { mutableStateOf(true) }                     // Alphabetical sort trigger

    // How Playlist are sorted
    val displayList =  when (sortIndex) {
            0 -> playlist.songs
            1 -> if (isAlphaAsc) playlist.songs.sortedBy { it.title } else playlist.songs.sortedByDescending { it.title }
            else -> playlist.songs
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(primaryContainerLight)
    ) {
        //Home
        HomeProfilePart(onHomeClick = { onHomeClick() })
        Spacer(modifier = Modifier.height(10.dp))

        // Playlist Description
        Text(
            text = playlist.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            // First row for even Labels
            Row(verticalAlignment = Alignment.CenterVertically) {
                playlist.labels.forEachIndexed { index, label ->
                    if (index % 2 == 0) {
                        Text(text = label.name, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(4.dp))
                        Box(Modifier.size(16.dp).background(label.color).border(1.dp, Color.Black))
                        Spacer(Modifier.width(12.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Second row for odd Labels
            Row(verticalAlignment = Alignment.CenterVertically) {
                playlist.labels.forEachIndexed { index, label ->
                    if (index % 2 != 0) {
                        Text(text = label.name, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(4.dp))
                        Box(Modifier.size(16.dp).background(label.color).border(1.dp, Color.Black))
                        Spacer(Modifier.width(12.dp))
                    }
                }
            }
        }

        // Song Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // Top: Segmented buttons
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
                    .background(onPrimaryLight)
            ) {
                val count = 2
                val theShape = RoundedCornerShape(0.dp)
                // Custom (Draggable)
                SegmentedButton(
                    selected = sortIndex == 0,
                    onClick = { sortIndex = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count, baseShape = theShape)
                ) { Text("Custom") }

                // Alphabetical (A-Z & Z-A)
                SegmentedButton(
                    selected = sortIndex == 1,
                    onClick = {
                        if (sortIndex == 1) isAlphaAsc = !isAlphaAsc
                        else sortIndex = 1
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count, baseShape = theShape)
                ) { Text(if (sortIndex == 1) if (isAlphaAsc) "A-Z" else "Z-A" else "Alphabetical") }

            }
            // Middle: LazyColumn for playlists
            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    playlistOfSongs.add(to.index, playlistOfSongs.removeAt(from.index))
                }
            )
            LazyColumn(
                state = reorderState.listState,
                contentPadding = PaddingValues(top = 8.dp),
                modifier = Modifier
                    .background(onPrimaryLight)
                    .fillMaxWidth()
                    .weight(1f)
                    .reorderable(reorderState),
            ) {
                items(
                    items = displayList,
                    key = { it.id }
                ) { song ->
                    ReorderableItem(
                        state = reorderState,
                        key = song.id
                    ) { isDragging ->
                        Button(
                            onClick = {},
                            contentPadding = PaddingValues(start = 10.dp, end = 10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                                .background(if (isDragging) Color.LightGray else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = song.title,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                if(sortIndex == 0){
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .detectReorder(reorderState)
                                    ) { Text("=", fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }
            // Floating + button: button selected to create a new playlist
            if(playlist.id != 1){
                Box(modifier = Modifier.fillMaxWidth()
                    .background(onPrimaryLight)
                ) {
                    ElevatedButton(
                        onClick = { addSong = true },
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text("+")
                    }
                }
            }
        }

        if (addSong) {
            var searchText by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(true) }

            // Recompute filtered songs whenever user types or adds a song
            val filteredSongs = remember(searchText, playlist.songs) {
                allSongs.songs.filter { song ->
                    song.title.contains(searchText, ignoreCase = true) &&
                            playlist.songs.none { it.id == song.id }
                }
            }
            AlertDialog(
                onDismissRequest = { addSong = false },
                confirmButton = {},
                dismissButton = {
                    Button(onClick = { addSong = false }) { Text("Done") }
                },
                text = { Column() {
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
            )
        }

        //Buttons
        BottomButtons()
    }
}

/*
//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlaylistPagePreview() {
    AppTheme {
        PlaylistPage(
            allSongs = Playlist(
                id = 1,
                name = "All Songs",
                labels = emptyList(),
                songs = listOf(
                    Song(1, "Creep"),
                    Song(2, "Candy"),
                    Song(3, "Amber"),
                    Song(4, "311"),
                    Song(5, "Tu Falta De Querer")
                )
            ),
            playlist = Playlist(
                id = 2,
                name = "My Favorites",
                labels = listOf(Label(Color.Red, "Rock"), Label(Color.Blue, "Relax")),
                songs = listOf(Song(1, "Sample Song"))
            ),
        )
    }
}
*/