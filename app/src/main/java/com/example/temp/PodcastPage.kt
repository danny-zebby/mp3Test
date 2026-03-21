package com.example.temp

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
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

// I need to fix the label sorting and stuff
// General Page finished
@Composable
fun PodcastPage(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
){

    var isAlphaAsc by remember { mutableStateOf(true) }         // Alphabetical sort trigger
    var lFC by remember { mutableStateOf(Color.Transparent) }  // Color to sort by
    var showColorMenu by remember { mutableStateOf(false) }     // Label sort trigger

    var sortIndex by remember { mutableIntStateOf(0) }                      // 0: Custom, 1: Alpha, 2: Label
    val displayList = when (sortIndex) {
        0 -> pOP.playlistOfPlaylist[0].mp3s
        1 -> if (isAlphaAsc) pOP.playlistOfPlaylist[0].mp3s.sortedBy { it.title }
            else pOP.playlistOfPlaylist[0].mp3s.sortedByDescending { it.title }
        2 -> {
            pOP.playlistOfPlaylist[0].mp3s.sortedWith(
                compareByDescending<MP3> { p -> p.labels.any { it.color == lFC } }
                    .thenBy { it.labels.joinToString { l -> l.name } }
                    .thenBy { it.title }
            )
        }
        else -> pOP.playlistOfPlaylist[0].mp3s
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            if (sortIndex != 0) return@rememberReorderableLazyListState
            if (from.index == 0 || to.index == 0) return@rememberReorderableLazyListState
            pOP.playlistOfPlaylist.add(to.index, pOP.playlistOfPlaylist.removeAt(from.index))
        }
    )

    // The whole page
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(primaryBGLight)
    ) {
        // Top of page
        TopBar(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = pOP.playlistOfPlaylist[0].name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
        )


        // Pod Area: top, mid, bot
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
                    .background(tertiaryBGLight)
            ) {
                val count = 3
                val theShape = RoundedCornerShape(0.dp)

                // Custom (Draggable)
                SegmentedButton(
                    selected = sortIndex == 0,
                    onClick = { sortIndex = 0 },
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
                        if (sortIndex == 1) isAlphaAsc = !isAlphaAsc
                        else sortIndex = 1
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count, baseShape = theShape),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF196D8A),
                        activeContentColor = Color.White
                    )
                ) { Text(if (sortIndex == 1) if (isAlphaAsc) "A-Z" else "Z-A" else "Alphabetical") }

                // Label grouping
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
                                Box(Modifier.size(12.dp).background(lFC).border(0.5.dp, Color.Black))
                            }
                            // DropdownMenu to pick label to sort by
                            DropdownMenu(
                                expanded = showColorMenu,
                                onDismissRequest = { showColorMenu = false }
                            ) {
                                pOP.playlistOfPlaylist[0].labels.forEach { label ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(Modifier.size(16.dp).background(label.color).border(1.dp, Color.Black))
                                                Spacer(Modifier.width(8.dp))
                                                Text(label.name)
                                            }
                                        },
                                        onClick = {
                                            lFC = label.color
                                            showColorMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }

            // Middle: LazyColumn for pOP
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
                    items = if(sortIndex == 0) pOP.playlistOfPlaylist[0].mp3s
                    else displayList,
                    key = { it.id }
                ) { podcast ->
                    ReorderableItem(
                        state = reorderState,
                        key = podcast.id
                    ) { isDragging ->
                        if( podcast.id >= 2 ){
                            Button(
                                onClick = { AudioPlayer.play(podcast, pOP.playlistOfPlaylist[0], displayList) },
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
                                    // Makes the playlist draggable
                                    if(sortIndex == 0 && podcast.id != 2){ // Checking if the sort is custom and not for All Songs
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .detectReorder(reorderState)
                                        ) { Text("=", fontWeight = FontWeight.Bold) }
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text( // Playlist names
                                        text = podcast.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.width(250.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    // This adds the labels
                                    if (podcast.labels.isNotEmpty() && podcast.id != 2) { // Checking if playlist has labels
                                        Row {
                                            podcast.labels.forEach { label ->
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
                                }
                            }
                        }
                    }
                }
            }
        }

        // Buttons
        BottomButtons()
    }
}

// Preview PodPage
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PodcastPagePreview() {
    NewTheme {
        PodcastPage()
    }
}