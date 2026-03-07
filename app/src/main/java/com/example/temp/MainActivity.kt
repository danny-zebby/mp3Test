package com.example.temp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.onPrimaryLight
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.collections.sortedBy
import androidx.compose.runtime.SideEffect
import com.example.compose.primaryContainerLight

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val window = this.window

                val statColor = Color(0xFFBFC2FF)
                val navColor = Color(0xFF272b60)

                SideEffect {
                    window.statusBarColor = statColor.toArgb()
                    window.navigationBarColor = navColor.toArgb()
                }

                var currentScreen by remember { mutableStateOf("home") }
                var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }
                
                // Hoisted State
                val AllSongs = mutableStateListOf(
                    Song(1,"Creep"), Song(2,"Candy"), Song(3,"Amber"),
                    Song(4, "311"), Song(5, "Tu Falta De Querer")
                )
                val playlistOfPlaylist = mutableStateListOf(
                    Playlist(id = 1, name = "All Songs", songs = AllSongs)
                )
                var nextPlaylistId by remember { mutableStateOf(2) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = statColor
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> MP3Home(
                            playlistOfPlaylist = playlistOfPlaylist,
                            onPlaylistClick = { playlist ->
                                selectedPlaylistId = playlist.id
                                currentScreen = "playlist"
                            },
                            onAddPlaylist = { name, labels ->
                                playlistOfPlaylist.add(Playlist(id = nextPlaylistId, name = name, labels = labels))
                                nextPlaylistId++
                            },
                            onHomeClick = { currentScreen = "home" },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "playlist" -> {
                            val playlist = playlistOfPlaylist.find { it.id == selectedPlaylistId }
                            playlist?.let { currentPlaylist ->
                                PlaylistPage(

                                    allSongs = playlistOfPlaylist.first { it.id == 1 },
                                    playlist = currentPlaylist,

                                    onAddSong = { song ->
                                        if (currentPlaylist.songs.none { it.id == song.id }) {
                                            currentPlaylist.songs.add(song)
                                        }
                                    },

                                    onHomeClick = {
                                        currentScreen = "home"
                                    },

                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Song(
    val id: Int,
    val title: String,
)

data class Label(
    val color: Color,
    val name: String,
)

data class Playlist(
    val id: Int,
    val name: String,
    val labels: List<Label> = emptyList(),
    val songs: SnapshotStateList<Song> = mutableStateListOf()
)

@Composable
fun MP3Home(
    playlistOfPlaylist: SnapshotStateList<Playlist>,
    onPlaylistClick: (Playlist) -> Unit = {},
    onAddPlaylist: (String, List<Label>) -> Unit = { _, _ -> },
    onHomeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var newItem by remember { mutableStateOf(TextFieldValue()) }                            // This stores text field text
    var selectedLabels by remember { mutableStateOf(listOf<Label>()) }                      // Temporary placement for labels
    var createPlaylist by remember { mutableStateOf(false) }                                // Tigger for create playlist
    var playlistLabel by remember { mutableStateOf(false) }                                 // Tigger for label dropdown (create)

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
    var sortIndex by remember { mutableStateOf(0) }                         // 0: Custom, 1: Alpha, 2: Label
    var isAlphaAsc by remember { mutableStateOf(true) }                     // Alphabetical sort trigger
    var labelFilterColor by remember { mutableStateOf(Color.Transparent) }  // Color to sort by
    var showColorMenu by remember { mutableStateOf(false) }                 // Label sort trigger

    // How Playlist are sorted
    val displayList = remember(playlistOfPlaylist.toList(), sortIndex, isAlphaAsc, labelFilterColor) {
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

    //The whole page
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(primaryContainerLight)
    ) {
        // First row: Home button, Music Playing, and Profile page
        HomeProfilePart(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Row: Using HorizontalPager to swipe in groups of three
        val navButtons = listOf(
            "View Songs", "Drive Mode", "Podcast Mode",
            "Button 4", "Button 5", "Button 6",
            "Button 7", "Button 8", "Button 9")
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
                            onClick = {},
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

        // Pager Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }

        // Playlist Area
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
                val count = 3
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

                // Label Grouping
                SegmentedButton(
                    selected = sortIndex == 2,
                    onClick = {
                        sortIndex = 2
                        showColorMenu = true
                        },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count, baseShape = theShape),
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
            // Middle: LazyColumn for playlists
            val reorderState = rememberReorderableLazyListState(
                onMove = { from, to ->
                    if (sortIndex != 0) return@rememberReorderableLazyListState
                    // Prevent "All Songs" from moving
                    if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState
                    playlistOfPlaylist.add(to.index, playlistOfPlaylist.removeAt(from.index))
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
                                Text(
                                    text = playlist.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(250.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                // This adds the labels and draggables
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
                                if(sortIndex == 0 && playlist.id != 1){ // Checking if the sort is custom and not for All Songs
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
                        Row () {
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

        // Bottom Controls: Each button takes 1/5th of the width
        BottomButtons()
    }
}

//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MP3Preview() {
    AppTheme {
        MP3Home(playlistOfPlaylist = remember { mutableStateListOf(Playlist(id = 1, name = "All Songs")) })
    }
}
