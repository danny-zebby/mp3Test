package com.example.temp

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.temp.ui.theme.TempTheme

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
    val itemList = remember { mutableStateListOf<String>() }
    var newItem by remember { mutableStateOf(TextFieldValue()) }
    var createPlaylist by remember { mutableStateOf(false) }

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
        Box(
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth() .padding(horizontal = 5.dp),
                        contentPadding = PaddingValues(start= 10.dp)
                    ) {
                        Text(text = "All Songs",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start)
                    }
                }
                items(itemList) { item ->
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(start= 10.dp)
                    ) {
                        Text(text = item,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start)
                    }
                }
            }
            ElevatedButton(
                onClick = { createPlaylist = true },
                modifier = Modifier.align(Alignment.BottomEnd) .padding(5.dp)
            ) {
                Text("+")
            }
        }

        if (createPlaylist) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { createPlaylist = false },
                confirmButton = {
                    Button(onClick = {
                        if (newItem.text.isNotBlank()) {
                            itemList.add(newItem.text)
                            newItem = TextFieldValue("")
                        }
                        createPlaylist = false
                    }) { Text("Add") }
                },
                dismissButton = {
                    Button(onClick = { createPlaylist = false }) { Text("Cancel") }
                },
                text = {
                    OutlinedTextField(
                        value = newItem,
                        onValueChange = { newItem = it },
                        label = { Text("Enter Song") }
                    )
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
