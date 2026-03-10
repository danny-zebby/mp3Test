package com.example.temp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.compose.primaryBGLight
import com.example.compose.tertiaryBGLight
import com.example.temp.ui.theme.NewTheme
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.collections.plus

@Composable
fun MP3Home(
    modifier: Modifier = Modifier,
    playlistOfPlaylist: SnapshotStateList<Playlist>,
    onDriveModeClick: () -> Unit = {},
    onPlaylistClick: (Playlist) -> Unit = {},
    onAddPlaylist: (String, List<Label>) -> Unit = { _, _ -> },
    onDeletePlaylist: (Int) -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    var newItem by remember { mutableStateOf(TextFieldValue()) }            // This stores text field text
    var selectedLabels by remember { mutableStateOf(listOf<Label>()) }      // Temporary placement for labels
    var createPlaylist by remember { mutableStateOf(false) }                // Tigger for create playlist
    var playlistLabel by remember { mutableStateOf(false) }                 // Tigger for label dropdown (create)
    var deletePlaylist by remember { mutableStateOf(false) }                // Tigger for deleting playlist
    var playlistToDelete by remember {mutableIntStateOf(-1)}                                                       // Temp placement for deleted playlist

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

    // Values used to create three playlist sorts
    var sortIndex by remember { mutableIntStateOf(0) }                         // 0: Custom, 1: Alpha, 2: Label
    var isAlphaAsc by remember { mutableStateOf(true) }                     // Alphabetical sort trigger
    var labelFilterColor by remember { mutableStateOf(Color.Transparent) }  // Color to sort by
    var showColorMenu by remember { mutableStateOf(false) }                 // Label sort trigger

    val displayList = remember(playlistOfPlaylist.size, sortIndex, isAlphaAsc, labelFilterColor) {
        val allSongs = playlistOfPlaylist.find { it.id == 1 }
        val others = playlistOfPlaylist.filter { it.id != 1 }

        val sortedOthers = when (sortIndex) {
            0 -> others
            1 -> if (isAlphaAsc) others.sortedBy { it.name } else others.sortedByDescending { it.name }
            2 -> {
                others.sortedWith(
                    compareByDescending<Playlist> { p -> p.labels.any { it.color == labelFilterColor } }
                        .thenBy { it.labels.joinToString { l -> l.name } }
                        .thenBy { it.name }
                )
            }
            else -> others
        }
        if (allSongs != null) listOf(allSongs) + sortedOthers else sortedOthers
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(primaryBGLight)
    ) {
        HomeProfilePart(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))

        val navButtons = listOf(
            "View Songs", "Drive Mode", "Podcast Mode",
            "Button 4", "Button 5", "Button 6",
            "Button 7", "Button 8", "Button 9")
        val buttonFunctions = listOf(
            {}, {onDriveModeClick()}, {},
            {}, {}, {},
            {}, {}, {})
        val pagerState = rememberPagerState(pageCount = { (navButtons.size + 2) / 3 })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val startIndex = page * 3
                for (i in 0 until 3) {
                    val index = startIndex + i
                    if (index < navButtons.size) {
                        Button(
                            onClick = buttonFunctions[index],
                            modifier = Modifier
                                .weight(1f)
                                .height(125.dp)
                        ) {
                            Text(
                                text = navButtons[index],
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFF196D8A) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }

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
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
                    .background(tertiaryBGLight)
            ) {
                val count = 3
                val theShape = RoundedCornerShape(0.dp)
                SegmentedButton(
                    selected = sortIndex == 0,
                    onClick = { sortIndex = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count, baseShape = theShape),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF196D8A),
                        activeContentColor = Color.White
                    )
                ) { Text("Custom") }

                SegmentedButton(
                    selected = sortIndex == 1,
                    onClick = {
                        if (sortIndex == 1) isAlphaAsc = !isAlphaAsc
                        else sortIndex = 1
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count, baseShape = theShape),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF196D8A),
                        activeContentColor = Color.White
                    )
                ) { Text(if (sortIndex == 1) if (isAlphaAsc) "A-Z" else "Z-A" else "Alphabetical") }

                SegmentedButton(
                    selected = sortIndex == 2,
                    onClick = {
                        sortIndex = 2
                        showColorMenu = true
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count, baseShape = theShape),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF196D8A),
                        activeContentColor = Color.White
                    ),
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Label")
                            if (sortIndex == 2) {
                                Spacer(Modifier.width(4.dp))
                                Box(Modifier.size(12.dp).background(labelFilterColor).border(0.5.dp, Color.Black))
                            }
                            // DropdownMenu to pick label to sort by
                            DropdownMenu(
                                expanded = showColorMenu,
                                onDismissRequest = { showColorMenu = false }
                            ) {
                                availableLabels.forEach { label ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(Modifier.size(16.dp).background(label.color).border(1.dp, Color.Black))
                                                Spacer(Modifier.width(8.dp))
                                                Text(label.name)
                                            }
                                        },
                                        onClick = {
                                            labelFilterColor = label.color
                                            showColorMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    if (sortIndex != 0) return@rememberReorderableLazyListState
                    if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState
                    playlistOfPlaylist.add(to.index, playlistOfPlaylist.removeAt(from.index))
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
                    items = if(sortIndex == 0) playlistOfPlaylist
                    else displayList,
                    key = { it.id }
                ) { playlist ->
                    ReorderableItem(
                        state = reorderState,
                        key = playlist.id
                    ) { isDragging ->
                        Button(
                            onClick = { onPlaylistClick(playlist) },
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
                                // Makes the playlist draggable
                                if(sortIndex == 0 && playlist.id != 1){ // Checking if the sort is custom and not for All Songs
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .detectReorder(reorderState)
                                    ) { Text("=", fontWeight = FontWeight.Bold) }
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = playlist.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(250.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                // This adds the labels
                                if (playlist.labels.isNotEmpty()) { // Checking if playlist has labels
                                    Row {
                                        playlist.labels.forEach { label ->
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(label.color)
                                                    .border(1.dp, Color.Black)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                    }
                                }
                                if(playlist.id != 1){
                                    Text(
                                        text = "X",
                                        modifier = Modifier.clickable{
                                            playlistToDelete = playlist.id
                                            deletePlaylist = true}
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Floating + button: button selected to create a new playlist
            Box(modifier = Modifier.fillMaxWidth()
                .background(tertiaryBGLight)
            ) {
                ElevatedButton(
                    onClick = { createPlaylist = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCF2F4)),
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
                            onAddPlaylist(newItem.text, selectedLabels)
                            newItem = TextFieldValue("")
                            selectedLabels = emptyList()
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
                        Row {
                            Button(
                                onClick = { playlistLabel = true},
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                if (playlistLabel) Text("Label Color V")
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
                                            if (!selectedLabels.any { it.color == label.color }) {
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
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { deletePlaylist = false },
                // confirmButton
                confirmButton = {
                    Button(onClick = {
                        onDeletePlaylist(playlistToDelete)
                        deletePlaylist = false
                    }) { Text("Delete") }
                },
                // dismissButton
                dismissButton = {
                    Button(onClick = { deletePlaylist = false }) { Text("Cancel") }
                },
                // Text: text field, label dropdown, labels selected
                text = {
                    Text(text = "Are you sure you want to delete " + playlistOfPlaylist.find { it.id ==  playlistToDelete}?.name)
                }
            )
        }
        BottomButtons()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MP3Preview() {
    NewTheme {
        MP3Home(playlistOfPlaylist = remember { mutableStateListOf(Playlist(id = 1, name = "All Songs")) })
    }
}
