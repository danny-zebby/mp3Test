package com.example.temp

import android.R.attr.fontWeight
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.temp.ui.theme.TempTheme
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.collections.sortedBy

data class Song(
    val title: String,
)

data class Playlist(
    val id: Int,
    val name: String,
    val labels: List<Color> = emptyList(),
    val songs: List<Song> = emptyList()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempTheme {
                // Pass the inner padding to the MP3 composable to handle system bars
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MP3(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MP3(modifier: Modifier = Modifier) {
    // Values use to ceate playlist
    val playlistOfPlaylist = remember { mutableStateListOf(Playlist(id = 1, name = "All Songs",labels = listOf(Color.Transparent)))}
    var nextPlaylistId by remember { mutableStateOf(2) }
    var newItem by remember { mutableStateOf(TextFieldValue()) }
    var selectedColors by remember { mutableStateOf(listOf<Color>()) }
    var createPlaylist by remember { mutableStateOf(false) }
    var playlistLabel by remember { mutableStateOf(false) }
    // Values used to create color sorting
    val colorNames = linkedMapOf(Color.Red to "Red", Color.Yellow to "Yellow", Color.Green to "Green", Color.Cyan to "Cyan", Color.Blue to "Blue", Color.Magenta to "Magenta", Color.White to "White", Color.Gray to "Gray", Color.Black to "Black")
    val colorOrder = colorNames.keys
        .withIndex()
        .associate { it.value to it.index }
    fun sortColors(colors: List<Color>): List<Color> {
        return colors.sortedBy{ color ->
            colorOrder[color] ?:
            Int.MAX_VALUE
        }
    }
    // Values used to create three playlist sorts
    var sortIndex by remember { mutableStateOf(0) } // 0: Custom, 1: Alpha, 2: Label
    var isAlphaAsc by remember { mutableStateOf(true) }
    var labelFilterColor by remember { mutableStateOf(Color.Transparent) }
    var showColorMenu by remember { mutableStateOf(false) }
    val colors = listOf(
        Color.Transparent, Color.Red, Color.Blue, Color.Green,
        Color.Cyan, Color.Gray, Color.Magenta, Color.Yellow,
        Color.White, Color.Black
    )
    val namesOfColors = listOf(
        "None", "Red", "Blue", "Green", "Cyan", "Gray", "Magenta", "Yellow", "White", "Black"
    )
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

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // First row: Using weight ensures the buttons fit any screen width
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { },
                modifier = Modifier.size(60.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Image(
                    painter = painterResource(R.drawable.home),
                    contentDescription = "Home",
                    modifier = Modifier.size(60.dp) // Scaled down for better fit
                )
            }
            Button(
                onClick = { },
                modifier = Modifier.weight(1f).height(60.dp) // weight(1f) fills remaining space
            ) {
                Text("Music Play Here", textAlign = TextAlign.Center)
            }
            OutlinedButton(
                onClick = { },
                modifier = Modifier.size(60.dp),
                border = BorderStroke(2.dp, Color.Black),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

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
                        // Spacer to maintain layout consistency for incomplete pages
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
        // 🔹 Playlist Area Refactor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)   // take remaining vertical space
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
            ) {
                // Custom (Draggable)
                SegmentedButton(
                    selected = sortIndex == 0,
                    onClick = { sortIndex = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) { Text("Custom") }

                // Alphabetical (A-Z & Z-A)
                SegmentedButton(
                    selected = sortIndex == 1,
                    onClick = {
                        if (sortIndex == 1) isAlphaAsc = !isAlphaAsc
                        else sortIndex = 1
                        playlistOfPlaylist.sortedBy { playlistOfPlaylist[0].name}
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) { Text(if (sortIndex == 1) if (isAlphaAsc) "A-Z" else "Z-A" else "Alphabetical") }

                // Label Grouping
                SegmentedButton(
                    selected = sortIndex == 2,
                    onClick = {
                        sortIndex = 2
                        showColorMenu = true
                        },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Label")
                            if (sortIndex == 2 && labelFilterColor != Color.Transparent) {
                                Spacer(Modifier.width(4.dp))
                                Box(Modifier.size(12.dp).background(labelFilterColor).border(0.5.dp, Color.Black))
                            }
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

            Spacer(modifier = Modifier.height(8.dp))

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
                contentPadding = PaddingValues(top = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // take remaining vertical space
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
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                if (playlist.id != 1) { // show drag handle for other playlists
                                    if (playlist.labels.isNotEmpty()) {
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
            }

            // Bottom-end: Floating + button
            Box(modifier = Modifier.fillMaxWidth()
                .padding(10.dp)
            ) {
                ElevatedButton(
                    onClick = { createPlaylist = true },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text("+")
                }
            }
        }
        if (createPlaylist) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { createPlaylist = false },
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
                dismissButton = {
                    Button(onClick = { createPlaylist = false }) { Text("Cancel") }
                },
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val controlModifier = Modifier.weight(1f).height(100.dp)
            val symbols = listOf("<<", "<", "||", ">", ">>")
            symbols.forEach { symbol ->
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = controlModifier
                ) {
                    Text(text = symbol)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true) // Added showSystemUi to match phone
@Composable
fun MP3Preview() {
    TempTheme {
        MP3()
    }
}
