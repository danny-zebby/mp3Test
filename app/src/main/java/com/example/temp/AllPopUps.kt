package com.example.temp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.compose.gridColors
import kotlin.collections.plus

val labelOrderMap = pOP.playlistOfPlaylist[3].labels.withIndex().associate { it.value.color to it.index }
fun sortLabels(labels: List<Label>): List<Label> {
    return labels.sortedBy { label ->
        labelOrderMap[label.color] ?: Int.MAX_VALUE
    }
}

//
@Composable
fun CreateLabel(
    onDismiss: () -> Unit,
    onColorPicked: (Label) -> Unit,
){
    var colorSelected by remember { mutableStateOf(Color.Transparent) }
    var labelName by remember { mutableStateOf(TextFieldValue()) }
    val label = pOP.playlistOfPlaylist[3].labels.find { it.color == colorSelected }
    if (label != null) labelName = TextFieldValue(label.name)
    else labelName = TextFieldValue("")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {Button(onClick = {
            if(labelName.text.isNotBlank() && colorSelected != Color.Transparent && label == null){
                onColorPicked(Label(colorSelected, labelName.text))
                labelName = TextFieldValue("")
                colorSelected = Color.Transparent
                onDismiss() } } ) { Text(stringResource(R.string.add)) }},
        dismissButton = { Button(onClick = {
            labelName = TextFieldValue("")
            colorSelected = Color.Transparent
            onDismiss()}) { Text(stringResource(R.string.cancel)) } },
        text = {
            Column {
                Text(stringResource(R.string.pickColorLabel))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = labelName,
                    onValueChange = { if(label == null) labelName = it else labelName = labelName },
                    label = { Text(stringResource(R.string.labelName)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    gridColors.forEach { colors ->
                        Column {
                            colors.forEach { color->
                                val br = if(colorSelected == color) gridColors[0][0] else gridColors[0][9]
                                Box( modifier = Modifier
                                    .size(20.dp)
                                    .background(color)
                                    .border(1.dp,br)
                                    .clickable{colorSelected = color}
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

//
@Composable
fun DeletePlaylist(
    onDismiss: () -> Unit,
    onDeletePlaylist: (Boolean) -> Unit,
    onId: Int
){
    AlertDialog(
        onDismissRequest = onDismiss,
        // confirmButton
        confirmButton = {
            Button(onClick = { onDeletePlaylist(true); onDismiss() })
            { Text(stringResource(R.string.delete)) }
        },
        // dismissButton
        dismissButton = {
            Button(onClick = { onDeletePlaylist(false); onDismiss() })
            { Text(stringResource(R.string.cancel)) }
        },
        // Text: text field, label dropdown, labels selected
        text = {
            Text(text = stringResource(R.string.deleteSure) +
                    pOP.playlistOfPlaylist.find { it.id ==  onId}?.name)
        }
    )
}

//
@Composable
fun EditAddPlaylist(
    onDismiss: () -> Unit,
    onPlaylistInfo: (String, List<Label>) -> Unit,
    onDeletePlaylist: (Boolean) -> Unit,
    onPlaylist: Playlist? = null, // need to edit this for laylist page only, not home
    onEdit: Boolean
){
    var newItem by remember {mutableStateOf(TextFieldValue("")) }     // This stores text field text
    if(onPlaylist != null) newItem = TextFieldValue(onPlaylist.name)         // When edit, playlist already has a name
    var selectedLabels by remember { mutableStateOf(listOf<Label>()) }      // Temporary placement for labels
    var playlistLabel by remember { mutableStateOf(false) }                 // Tigger for label dropdown (create)
    var showGrid by remember { mutableStateOf(false) }                      // Tigger for color grid


    AlertDialog(
        // Confirm & Dismiss Button Logic
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (newItem.text.isNotBlank()) {
                    onPlaylistInfo(newItem.text, selectedLabels)
                    newItem = TextFieldValue("")
                    selectedLabels = emptyList()
                }
                onDismiss()
            }) { if(onEdit) Text(stringResource(R.string.edit))
            else Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            if(onEdit) { Button( onClick = { onDeletePlaylist(true) } )
            { Text(stringResource(R.string.delete)) } }
            Button(onClick = { onDismiss() }) { Text(stringResource(R.string.cancel)) }
        },
        // Text: text field, label dropdown, labels selected, label creation
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text(stringResource(R.string.playlistName)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(
                        onClick = { playlistLabel = true},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        if (playlistLabel) Text(stringResource(R.string.labelDown))
                        else Text(stringResource(R.string.labelUp))
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
                    // None, New, etc
                    DropdownMenu(expanded = playlistLabel, onDismissRequest = { playlistLabel = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.none)) }, onClick = {
                            selectedLabels = emptyList(); playlistLabel = false })
                        DropdownMenuItem(text = { Text(stringResource(R.string.wen)) }, onClick = { showGrid = true } )
                        pOP.playlistOfPlaylist[3].labels.forEach { label ->
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
    // Color select from color grid PU
    if(showGrid){
        CreateLabel(
            onDismiss = { showGrid = false },
            onColorPicked = { label ->
                if (label.color != Color.Transparent){
                    pOP.playlistOfPlaylist[3].labels.add(label)
                    selectedLabels = selectedLabels + label
                }
            }
        )
    }
}

//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSong(
    onDismiss: () -> Unit,
    onPlaylist: Playlist,
    onReturnSong: (MP3) -> Unit,
){
    var searchText by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }

    // Recompute filtered songs whenever user types or adds a song
    val filteredSongs = remember(searchText, onPlaylist.mp3s) {
        pOP.playlistOfPlaylist[3].mp3s.filter { song ->
            song.title.contains(searchText, ignoreCase = true) &&
                    onPlaylist.mp3s.none { it.id == song.id }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = { Button(onClick = { onDismiss() }) { Text(stringResource(R.string.add)) } },
        text = {
            Column {
                SearchBar(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onSearch = {},
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text(stringResource(R.string.searchSong)) },
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
                                        onReturnSong(song)
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

@Composable
fun PodLabels(
    onDismiss: () -> Unit,
    onPodLabels: (List<Label>) -> Unit,
){
    var selectedLabels by remember { mutableStateOf(listOf<Label>()) }      // Temporary placement for labels
    var podLabel by remember { mutableStateOf(false) }                      // Tigger for label dropdown (create)
    var showGrid by remember { mutableStateOf(false) }                      // Tigger for color grid

    AlertDialog(
        // Confirm & Dismiss Button Logic
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onPodLabels(selectedLabels)
                selectedLabels = emptyList()
                onDismiss()
            } ) { Text(stringResource(R.string.add)) }
        },
        // Text: text field, label dropdown, labels selected, label creation
        text = {
            Row {
                Button(
                    onClick = { podLabel = true},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    if (podLabel) Text(stringResource(R.string.labelUp))
                    else Text(stringResource(R.string.labelDown))
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
                // None, New, etc
                DropdownMenu(expanded = podLabel, onDismissRequest = { podLabel = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.none)) }, onClick = {
                        selectedLabels = emptyList(); podLabel = false })
                    DropdownMenuItem(text = { Text(stringResource(R.string.wen)) }, onClick = { showGrid = true } )
                    pOP.playlistOfPlaylist[0].labels.forEach { label ->
                        DropdownMenuItem(
                            text = { Text(label.name) },
                            onClick = {
                                if(!selectedLabels.any { it.color == label.color }) {
                                    selectedLabels = selectedLabels + label
                                } else {
                                    selectedLabels = selectedLabels.filterNot { it.color == label.color }
                                }
                                podLabel = false
                            }
                        )
                    }
                }
            }
        }
    )
    // Color select from color grid PU
    if(showGrid){
        CreateLabel(
            onDismiss = { showGrid = false },
            onColorPicked = { label ->
                if (label.color != Color.Transparent){
                    pOP.playlistOfPlaylist[0].labels.add(label)
                    selectedLabels = selectedLabels + label
                }
            }
        )
    }
}