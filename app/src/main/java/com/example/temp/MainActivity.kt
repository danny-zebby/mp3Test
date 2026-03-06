package com.example.temp

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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val window = this.window
                val background = Color(0xFFE0E0FF)
                val navColor = Color(0xFF272b60)

                SideEffect {
                    window.statusBarColor = background.toArgb()
                    window.navigationBarColor = navColor.toArgb()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = background
                ) { innerPadding ->

                    MP3Home(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(background)
                    )
                }
            }
        }
    }
}

data class Song(
    val title: String,
)

data class Playlist(
    val id: Int,
    val name: String,
    val labels: List<Color> = emptyList(),
    val songs: List<Song> = emptyList()
)

@Composable
fun MP3Home(modifier: Modifier = Modifier) {
    // Values use to create playlist
    val playlistOfPlaylist = remember { mutableStateListOf(Playlist(id = 1, name = "All Songs"))}  // The main list
    var nextPlaylistId by remember { mutableStateOf(2) }                                    // This increments the playlist id
    var newItem by remember { mutableStateOf(TextFieldValue()) }                            // This stores text field text
    var selectedColors by remember { mutableStateOf(listOf<Color>()) }                      // Temporary placement for labels
    var createPlaylist by remember { mutableStateOf(false) }                                // Tigger for create playlist
    var playlistLabel by remember { mutableStateOf(false) }                                 // Tigger for label dropdown (create)
    // Values used to create color sorting
    val colorNames = linkedMapOf(Color.Red to "Red",    // Used to make colorOrder
        Color.Yellow to "Yellow", Color.Green to "Green", Color.Cyan to "Cyan", Color.Blue to "Blue", Color.Magenta to "Magenta", Color.White to "White", Color.Gray to "Gray", Color.Black to "Black")
    val colorOrder = colorNames.keys                    // Used to make sortColor
        .withIndex()
        .associate { it.value to it.index }
    fun sortColors(colors: List<Color>): List<Color> { // Sorts the color in the order I listed above in colorNames
        return colors.sortedBy{ color ->
            colorOrder[color] ?:
            Int.MAX_VALUE
        }
    }
    // Values used to create three playlist sorts
    var sortIndex by remember { mutableStateOf(0) }                         // 0: Custom, 1: Alpha, 2: Label
    var isAlphaAsc by remember { mutableStateOf(true) }                     // Alphabetical sort trigger
    var labelFilterColor by remember { mutableStateOf(Color.Transparent) }  // Color to sort by
    var showColorMenu by remember { mutableStateOf(false) }                 // Label sort trigger
    val colors = listOf(                                                           // Color to make dropdown easier
        Color.Red, Color.Blue, Color.Green,
        Color.Cyan, Color.Gray, Color.Magenta, Color.Yellow,
        Color.White, Color.Black
    )
    val namesOfColors = listOf(                                                     // names of colors to make dropdown easier
        "Red", "Blue", "Green", "Cyan", "Gray", "Magenta", "Yellow", "White", "Black"
    )
    // How Playlist are sorted
    val displayList = remember(playlistOfPlaylist.toList(), sortIndex, isAlphaAsc, labelFilterColor) {
        when (sortIndex) {
            0 -> playlistOfPlaylist.toList()
            1 -> if (isAlphaAsc) playlistOfPlaylist.sortedBy { it.name } else playlistOfPlaylist.sortedByDescending { it.name }
            2 -> {
                playlistOfPlaylist.sortedWith(
                    compareByDescending<Playlist> { it.labels.contains(labelFilterColor)}
                        .thenBy { it.labels.toString() }
                        .thenBy { it.name }
                )
            }
            else -> playlistOfPlaylist.toList()
        }
    }

    //The whole page
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(primaryContainerLight)
    ) {
        // First row: Home button, Music Playing, and Profile page
        HomeProfilePart()
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
                        playlistOfPlaylist.sortedBy { playlistOfPlaylist[0].name}
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
                                colors.forEachIndexed { index, color ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(Modifier.size(16.dp).background(color).border(1.dp, Color.Black))
                                                Spacer(Modifier.width(8.dp))
                                                Text(namesOfColors[index])
                                            }
                                        },
                                        onClick = {
                                            labelFilterColor = color
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
                                    text = playlist.name,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                // This adds the labels and draggables
                                if (playlist.labels.isNotEmpty()) { // Checking if playlist has labels
                                    Row {
                                        playlist.labels.forEach { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(15.dp)
                                                    .background(color)
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
                            playlistOfPlaylist.add(
                                Playlist(
                                    id = nextPlaylistId,
                                    name = newItem.text,
                                    labels = selectedColors
                                )
                            )
                            nextPlaylistId++
                            newItem = TextFieldValue("")
                            selectedColors = emptyList()
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
                            selectedColors = sortColors(selectedColors)
                            for(i in selectedColors.indices){
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(selectedColors[i])
                                        .border(1.dp, Color.Black)

                                ){}
                            }
                            // "Red", "Yellow", "Green", "Cyan", "Blue", "Magenta", "White", "Gray", "Black"
                            DropdownMenu(expanded = playlistLabel, onDismissRequest = { playlistLabel = false }) {
                                DropdownMenuItem(text = { Text("None") }, onClick = {
                                    selectedColors = emptyList(); playlistLabel = false })
                                DropdownMenuItem(text = { Text("Red") }, onClick = {
                                    if(!selectedColors.contains(Color.Red))
                                    {selectedColors = selectedColors +  Color.Red}
                                    else
                                    {selectedColors = selectedColors - Color.Red}
                                    playlistLabel = false})
                                // Same as Red
                                DropdownMenuItem(text = { Text("Yellow") }, onClick = { if(!selectedColors.contains(Color.Yellow)) {selectedColors = selectedColors +  Color.Yellow} else {selectedColors = selectedColors - Color.Yellow}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("Green") }, onClick = { if(!selectedColors.contains(Color.Green)) {selectedColors = selectedColors +  Color.Green; } else {selectedColors = selectedColors - Color.Green}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("Cyan") }, onClick = { if(!selectedColors.contains(Color.Cyan)) {selectedColors = selectedColors +  Color.Cyan} else {selectedColors = selectedColors - Color.Cyan}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("Blue") }, onClick = { if(!selectedColors.contains(Color.Blue)) {selectedColors = selectedColors +  Color.Blue} else {selectedColors = selectedColors - Color.Blue}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("Magenta") }, onClick = { if(!selectedColors.contains(Color.Magenta)) {selectedColors = selectedColors +  Color.Magenta} else {selectedColors = selectedColors - Color.Magenta}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("White") }, onClick = { if(!selectedColors.contains(Color.White)) {selectedColors = selectedColors +  Color.White} else {selectedColors = selectedColors - Color.White}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("Gray") }, onClick = { if(!selectedColors.contains(Color.Gray)) {selectedColors = selectedColors +  Color.Gray} else {selectedColors = selectedColors - Color.Gray}; playlistLabel = false})
                                DropdownMenuItem(text = { Text("Black") }, onClick = { if(!selectedColors.contains(Color.Black)) {selectedColors = selectedColors +  Color.Black} else {selectedColors = selectedColors - Color.Black}; playlistLabel = false})
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
        MP3Home()
    }
}
