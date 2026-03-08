package com.example.temp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.temp.ui.theme.NewTheme

@Composable
fun DrivingMode(onHomeClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize()
    ){
        val controlModifier = Modifier.weight(1f).width(250.dp)
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { onHomeClick() }, shape = RectangleShape, modifier = controlModifier,)  { Text("Home") }
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Loop") }
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Prev Play") }
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Prev Song") }
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Repeat") }
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Pause") }
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Next Play") }
            Button(onClick = {}, shape = RectangleShape, modifier = controlModifier,)  { Text("Next Song") }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DrivingModePreview() {
    NewTheme {
        DrivingMode(onHomeClick = {})
    }
}
