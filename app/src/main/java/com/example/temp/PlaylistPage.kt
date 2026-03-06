package com.example.temp


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.onPrimaryLight
import com.example.compose.primaryContainerLight
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun PlaylistPage(modifier: Modifier = Modifier) {
    // Values use to create playlist
    val AllSongs = remember { mutableStateListOf(Song(1, "Creep"), Song(2, "Candy") , Song(3, "Amber"), Song(4, "311"), Song(5, "We can be heros"))}  // The main list
    val playlistOfSongs = remember { mutableStateListOf(Song(0,""))}  // The main list
    var nextPlaylistId by remember { mutableStateOf(1) }                                    // This increments the playlist id
    var newItem by remember { mutableStateOf(TextFieldValue()) }                            // This stores text field text
    var createPlaylist by remember { mutableStateOf(false) }                                // Tigger for create playlist
    // Values used to create three playlist sorts
    var sortIndex by remember { mutableStateOf(0) }                         // 0: Custom, 1: Alpha, 2: Label
    var isAlphaAsc by remember { mutableStateOf(true) }                     // Alphabetical sort trigger
    val colors = listOf(                                                           // Color to make dropdown easier
        Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta,
    )
    val namesOfColors = listOf(                                                     // names of colors to make dropdown easier
        "Red", "Yellow", "Green", "Cyan", "Blue", "Magenta",
    )
    val labelRows = listOf(1,2)
    // How Playlist are sorted
    val displayList = remember(playlistOfSongs.toList(), sortIndex, isAlphaAsc) {
        when (sortIndex) {
            0 -> playlistOfSongs.toList()
            1 -> if (isAlphaAsc) playlistOfSongs.sortedBy { it.title } else playlistOfSongs.sortedByDescending { it.title }
            else -> playlistOfSongs.toList()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(primaryContainerLight)
    ) {
        //Home
        HomeProfilePart()
        Spacer(modifier = Modifier.height(10.dp))

        // Playlist Description
        Text(
            text = "Playlist Name",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
        )
        Box(
        ) {
            Column(
            ) {
                labelRows.forEachIndexed { index, i ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        colors.forEachIndexed { index, color ->
                            Text(namesOfColors[index])
                            Box(Modifier.size(16.dp).background(color).border(1.dp, Color.Black))
                            Spacer(Modifier.width(8.dp))
                        }
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
                        playlistOfSongs.sortedBy { playlistOfSongs[0].title}
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count, baseShape = theShape)
                ) { Text(if (sortIndex == 1) if (isAlphaAsc) "A-Z" else "Z-A" else "Alphabetical") }

            }
            // Middle: LazyColumn for playlists
            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    // Prevent "All Songs" from moving
                    if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState
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
            // Floating + button: button selected to create a new playlist
            Box(modifier = Modifier.fillMaxWidth()
                .background(onPrimaryLight)
            ) {
                ElevatedButton(
                    onClick = { createPlaylist = true },
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    Text("+")
                }
            }
        }
        // Dialog screen pop up to create a new playlist
        if (createPlaylist) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { createPlaylist = false },
                // confirmButton
                confirmButton = {
                    Button(onClick = {
                        if (newItem.text.isNotBlank()) {
                            playlistOfSongs.add(
                                Song(
                                    id = nextPlaylistId,
                                    title = newItem.text,
                                )
                            )
                            nextPlaylistId++
                            newItem = TextFieldValue("")
                        }
                        createPlaylist = false
                    }) { Text("Add") }
                },
                // dismissButton
                dismissButton = {
                    Button(onClick = { createPlaylist = false }) { Text("Cancel") }
                },
                // Text: text field, label dropdown, labels selected
                text = {
                    Column {
                        OutlinedTextField(
                            value = newItem,
                            onValueChange = { newItem = it },
                            label = { Text("Playlist Name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            )
        }

        //Buttons
        BottomButtons()
    }
}


//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlaylistPagePreview() {
    AppTheme {
        PlaylistPage()
    }
}