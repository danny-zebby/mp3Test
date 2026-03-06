package com.example.temp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.onPrimaryLight

@Composable
fun HomeProfilePart(modifier: Modifier = Modifier) {
    // First row: Home button, Music Playing, and Profile page
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Button
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
                modifier = Modifier.size(60.dp)
            )
        }
        // Music Playing
        Box(
            modifier = Modifier.weight(1f).height(60.dp) // weight(1f) fills remaining space
        ) {
            var sliderPosition by remember { mutableFloatStateOf(0f) }
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it }
            )
            Text(text = sliderPosition.toString())
        }
        // Profile Button
        OutlinedButton(
            onClick = { },
            modifier = Modifier.size(60.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(onPrimaryLight),
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
}

//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeProfilePartPreview() {
    AppTheme {
        HomeProfilePart()
    }
}