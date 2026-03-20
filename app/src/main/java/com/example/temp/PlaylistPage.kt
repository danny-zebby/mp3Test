package com.example.temp

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.primaryBGLight
import com.example.compose.tertiaryBGLight
import com.example.temp.ui.theme.NewTheme

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPage(
    modifier: Modifier = Modifier,
    allSongs: Playlist,
    playlist: Playlist,
    onAddSong: (MP3) -> Unit,
    onRemoveSong: (MP3) -> Unit,
    onEditPlaylist: (String, List<Label>) -> Unit = { _, _ -> },
    onDeletePlaylist: (Int) -> Unit = {},
    onHomeClick: () -> Unit
) {
    // Values use to create playlist
    var addSong by remember { mutableStateOf(false) }

    // Values used to create three playlist sorts
    var sortIndex by remember { mutableIntStateOf(0) }                      // 0: Custom, 1: Alpha
    var isAlphaAsc by remember { mutableStateOf(true) }                     // Alphabetical sort trigger

    var newItem by remember { mutableStateOf(TextFieldValue(playlist.name)) }            // This stores text field text
    var editPlaylist by remember { mutableStateOf(false) }                // Tigger for create playlist
    var deletePlaylist by remember { mutableStateOf(false) }                // Tigger for deleting playlist

    var selectedLabels by remember { mutableStateOf(listOf<Label>()) }      // Temporary placement for labels
    var playlistLabel by remember { mutableStateOf(false) }                 // Tigger for label dropdown (create)
    val availableLabels = listOf(
        Label(Color.Red, "Red"), Label(Color.Yellow, "Yellow"), Label(Color.Green, "Green"),
        Label(Color.Cyan, "Cyan"), Label(Color.Blue, "Blue"), Label(Color.Magenta, "Magenta")
    )
    val labelOrderMap = availableLabels.withIndex().associate { it.value.color to it.index }
    fun sortLabels(labels: List<Label>): List<Label> {
        return labels.sortedBy { label ->
            labelOrderMap[label.color] ?: Int.MAX_VALUE
        }
    }


    // How Playlist are sorted
    val displayList =  when (sortIndex) {
        0 -> playlist.mp3s
        1 -> if (isAlphaAsc) playlist.mp3s.sortedBy { it.title } else playlist.mp3s.sortedByDescending { it.title }
        else -> playlist.mp3s
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(primaryBGLight)
    ) {
        //Home
        TopBar(onHomeClick = { onHomeClick() })
        Spacer(modifier = Modifier.height(10.dp))

        // Playlist Description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp, end = 8.dp)
            ){
                Text(
                    text = playlist.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                )
                if(playlist.id != 0){
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {editPlaylist = true}) { Text("Edit")}
                }
            }
            // First row for even Labels
            Row(verticalAlignment = Alignment.CenterVertically) {
                playlist.labels.forEachIndexed { index, label ->
                    if (index % 2 == 0) {
                        Text(text = label.name, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(4.dp))
                        Box(Modifier
                            .size(16.dp)
                            .background(label.color)
                            .border(1.dp, Color.Black))
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
                        Box(Modifier
                            .size(16.dp)
                            .background(label.color)
                            .border(1.dp, Color.Black))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tertiaryBGLight)
            ) {
                val count = 2
                val theShape = RoundedCornerShape(0.dp)
                // Custom (Draggable)
                SegmentedButton(
                    selected = sortIndex == 0,
                    onClick = {
                                sortIndex = 0
//                                playlist.relist(sortIndex)
                              },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count, baseShape = theShape),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF196D8A),
                        activeContentColor = Color.White
                    )
                ) { Text("Custom") }

                // Alphabetical (A-Z & Z-A)
                SegmentedButton(
                    selected = sortIndex == 1,
                    onClick = {
                        if (sortIndex == 1) {
                            isAlphaAsc = !isAlphaAsc
//                            if (!isAlphaAsc) playlist.relist(sortIndex+1)
//                            else playlist.relist(sortIndex)
                        }
                        else {sortIndex = 1
//                            playlist.relist(sortIndex)
                        } },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count, baseShape = theShape),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF196D8A),
                        activeContentColor = Color.White
                    )
                ) { Text(if (sortIndex == 1) if (isAlphaAsc) "A-Z" else "Z-A" else "Alphabetical") }

            }
            // Middle: LazyColumn for playlists
            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    if (sortIndex == 0) {
                        playlist.mp3s.add(
                            to.index,
                            playlist.mp3s.removeAt(from.index)
                        )
                    }
                }
            )

            LazyColumn(
                state = reorderState.listState,
                contentPadding = PaddingValues(top = 8.dp),
                modifier = Modifier
                    .background(tertiaryBGLight)
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
                            onClick = {
                                AudioPlayer.play(song, playlist, displayList)
                            },
                            shape = RectangleShape,
                            contentPadding = PaddingValues(start = 10.dp, end = 10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                                .background(if (isDragging) Color.LightGray else Color.Transparent)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if(sortIndex == 0){
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .detectReorder(reorderState)
                                    ) { Text("=", fontWeight = FontWeight.Bold) }
                                }
                                Text(
                                    text = song.title,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                if(playlist.id != 0){
                                    Text(
                                        text = "X",
                                        modifier = Modifier.clickable { onRemoveSong(song) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Floating + button: button selected to create a new playlist
            if(playlist.id != 0){
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(tertiaryBGLight)
                ) {
                    ElevatedButton(
                        onClick = { addSong = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCF2F4)),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
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
            val filteredSongs = remember(searchText, playlist.mp3s) {
                allSongs.mp3s.filter { song ->
                    song.title.contains(searchText, ignoreCase = true) &&
                            playlist.mp3s.none { it.id == song.id }
                }
            }
            AlertDialog(
                onDismissRequest = { addSong = false },
                confirmButton = {},
                dismissButton = { Button(onClick = { addSong = false }) { Text("Done") } },
                text = {
                    Column {
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

        // Dialog screen pop up to edit current playlist
        if (editPlaylist) {
            AlertDialog(
                onDismissRequest = { editPlaylist = false },
                // confirmButton
                confirmButton = {
                    Button(onClick = {
                        if (newItem.text.isNotBlank()) {
                            onEditPlaylist(newItem.text, selectedLabels)
                            newItem = TextFieldValue("")
                            selectedLabels = emptyList()
                        }
                        editPlaylist = false
                    }) { Text("Edit") }
                },
                // dismissButton
                dismissButton = {
                    Button(onClick = { editPlaylist = false }) { Text("Cancel") }
                    Button(onClick = { editPlaylist = false }) { Text("Cancel") }
                },
                // Text: text field, label dropdown, labels selected
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            OutlinedTextField(
                                value = newItem,
                                onValueChange = { newItem = it },
                                label = { Text("Playlist Name") }
                            )
//                            Spacer(modifier = Modifier.width(3.dp))
                            Button(onClick = {deletePlaylist = true},shape = RectangleShape){Text("Delete")}
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { playlistLabel = true},
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                if (playlistLabel) Text("Label Color v")
                                else Text("Label Color ^")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            selectedLabels = sortLabels(selectedLabels)
                            for(i in selectedLabels.indices){
                                Box(
                                    modifier = Modifier
                                        .size(35.dp)
                                        .background(selectedLabels[i].color)
                                        .border(1.dp, Color.Black)

                                ){}
                            }
                            // "Red", "Yellow", "Green", "Cyan", "Blue", "Magenta"
                            DropdownMenu(expanded = playlistLabel, onDismissRequest = { playlistLabel = false }) {
                                DropdownMenuItem(text = { Text("None") }, onClick = {
                                    selectedLabels = emptyList(); playlistLabel = false })
                                availableLabels.forEach { label ->
                                    DropdownMenuItem(
                                        text = { Text(label.name) },
                                        onClick = {
                                            if(!selectedLabels.any { it.color == label.color }) {
                                                selectedLabels = selectedLabels + label
                                            } else {
                                                selectedLabels = selectedLabels.filterNot { it.color == label.color }
                                            }
                                            playlistLabel = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
        // delete playlist pop up
        if (deletePlaylist){
            DeletPlaylist(
                onDismiss = { deletePlaylist = false},
                onDeletePlaylist = { delete ->
                    if(delete) onDeletePlaylist(playlist.id)
                },
                onId = playlist.id
            )
        }

        //Buttons
        BottomButtons()
    }
}


//Preview App
@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlaylistPagePreview() {
    NewTheme {
        PlaylistPage(
            allSongs = Playlist(
                id = 0,
                name = "All Songs",
            ),
            playlist = Playlist(
                id = 1,
                name = "Some Songs",
                mp3s = listOf<MP3>( MP3(1,"Preview", "yes"), MP3(2,"These Nuts","") ) as SnapshotStateList<MP3>,
            ),
            onAddSong = {},
            onRemoveSong = {},
            onHomeClick = {}
        )
    }
}
