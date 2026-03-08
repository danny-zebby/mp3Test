package com.example.temp

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.inversePrimaryLight
import com.example.compose.onPrimaryLight
import com.example.compose.secondaryBCLight
import com.example.compose.secondaryBGLight
import com.example.compose.tertiaryBGLight
import com.example.temp.ui.theme.NewTheme

@Composable
fun HomeProfilePart(
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // First row: Home button, Music Playing, and Profile page
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(secondaryBGLight)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Button
        Button(
            onClick = onHomeClick,
            modifier = Modifier.size(60.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(R.drawable.home),
                contentDescription = "Home",
                modifier = Modifier.size(60.dp)
            )
        }
        // Music Playing
        Surface(
            shape = RoundedCornerShape(25.dp),
            color = secondaryBCLight,
            modifier = Modifier.weight(1f).height(80.dp)
        ) {
            var sliderPosition by remember { mutableFloatStateOf(0f) }
            Column() {
                Row() {
                    Slider(
                        value = sliderPosition,
                        // primaryBCLight & primaryBGLight
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.background,
                        ),
                        onValueChange = { sliderPosition = it }
                    )
                }
                Row(  ){
                    Text(
                        text = "   " + String.format("%.2f", sliderPosition),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = String.format("%.2f", (1-sliderPosition)) + "   ",
                    )
                }
            }

        }

        // Profile Button
        /*
        Stats of songs played
        Stats of time listening to (etc)
        Show device storage
        */
        OutlinedButton(
            onClick = onProfileClick,
            modifier = Modifier.size(60.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(tertiaryBGLight),
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
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 3.dp,
        color = Color.Black
    )
}

//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeProfilePartPreview() {
    NewTheme {
        HomeProfilePart()
    }
}
