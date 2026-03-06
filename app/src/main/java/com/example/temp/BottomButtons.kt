package com.example.temp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

@Composable
fun BottomButtons(modifier: Modifier = Modifier) {
    var toggle by remember { mutableStateOf(true) }
    // Bottom Controls: Each button takes 1/5th of the width
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val controlModifier = Modifier.weight(1f).height(100.dp)
        val symbols1 = listOf("C>", "||", ">", ">>")
        val symbols2 = listOf("O>", "||", "<", "<<")
        Button(
            onClick = {toggle = !toggle},
            shape = RectangleShape,
            modifier = controlModifier
        ) {
            if(toggle) Text("^")
            else Text("V")
        }
        if(toggle){
            symbols1.forEach { symbol ->
                Button(
                    onClick = {},
                    shape = RectangleShape,
                    modifier = controlModifier
                ) {
                    Text(text = symbol)
                }
            }
        }
        else{
            symbols2.forEach { symbol ->
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

//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomButtonsPreview() {
    AppTheme {
        BottomButtons()
    }
}