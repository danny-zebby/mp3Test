package com.example.temp

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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.temp.ui.theme.TempTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
        // The Column arranges the Rows vertically
        Column(
            modifier = Modifier
                .fillMaxSize() // Make the column fill the entire screen
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    // .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 🔹 Home Button
                Surface(
                    onClick = { },
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Image(
                        painter = painterResource(R.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier
                            .size(90.dp)
                            .padding(5.dp)
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
                /*
                    * Change
                    * To
                    * Profile
                    * Instead
                    * of
                    * Settings
                    * */
                // 🔹 Settings Button
                Surface(
                    onClick = { },
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Image(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .size(90.dp)
                            .padding(5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /*      Navagation Controller for App Funtioncs:
                    View Song
                    Drive Mode
                    Podcast Mode    */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )

            {
                Button(
                    onClick = {},
                    modifier = Modifier
                            .size(125.dp)
                ) {
                    Text(
                        text = "View Song"

                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .size(125.dp)
                ) {
                    Text(
                        text = "Drive Mode"

                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .size(125.dp)
                ) {
                    Text(
                        text = "Podcast Mode"

                    )
                }
            }


            /*      Bottom Row of Buttons:
                    Previous Playlist
                    Previous Song
                    Pause/Play
                    Next Song
                    Next Playlist       */
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Spacer(modifier = Modifier.height(50.dp))
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TempTheme {
        Greeting("Android")
    }
}