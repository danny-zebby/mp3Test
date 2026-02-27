package com.example.temp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.temp.ui.theme.TempTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MP3()
                }
            }
        }
    }
}
@Composable
fun MP3() {
        // The Column arranges the Rows vertically
        Column(
            modifier = Modifier
                .fillMaxSize() // Make the column fill the entire screen
        ) {
            // Make some space between the notifaction bar and app
            Spacer(modifier = Modifier.height(50.dp))

            // First row with Home, Profile, and music bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔹 Home Button
                Surface(
                    onClick = { },
                ) {
                    Image(
                        painter = painterResource(R.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier
                            .size(90.dp)
                    )
                }
                // 🔹 Music Button
                Button(
                    onClick = { },
                    modifier = Modifier
                        .size(270.dp, 90.dp)
                ) {
                    Text("Music Play Here")
                }
                // 🔹 Profile Button
                Surface(
                    onClick = { },
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(5.dp, Color.Black)
                ) {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(90.dp)
                           .padding(5.dp)
                    )
                }
            }

            //  Navagation Controller for App Funtioncs: View Songs, Drive Mode, Podcast Mode
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )

            {
                Spacer(modifier = Modifier.width(10.dp))
                // View Songs
                Button(
                    onClick = {},
                    modifier = Modifier
                            .size(125.dp)
                ) {
                    Text(
                        text = "View Songs",
                        textAlign = TextAlign.Center

                    )
                }
                // Drive Mode
                Button(
                    onClick = {},
                    modifier = Modifier
                        .size(125.dp)
                ) {
                    Text(
                        text = "Drive Mode",
                        textAlign = TextAlign.Center

                    )
                }
                // Podcast Mode
                Button(
                    onClick = {},
                    modifier = Modifier
                        .size(125.dp)
                ) {
                    Text(
                        text = "Podcas Mode",
                        textAlign = TextAlign.Center

                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            // Bottom Row of Buttons: Previous Playlist , Previous Song , Pause/Play , Next Song ,Next Playlist
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Playlist
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Text(
                        text = "<<"
                    )
                }
                // Previous Song
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Text(
                        text = "<"
                    )
                }
                // Pause/Play
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Text(
                        text = "||"
                    )
                }
                // Next Song
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Text(
                        text = ">"
                    )
                }
                // Next Playlist
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = Modifier
                        .size(100.dp)
                ) {
                    Text(
                        text = ">>"
                    )
                }
            }
            // Space between google home buttons and app
            Spacer(modifier = Modifier.height(50.dp))
        }
    }