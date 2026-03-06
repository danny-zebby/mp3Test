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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun BottomButtons(modifier: Modifier = Modifier) {
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

//Preview App
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomButtonsPreview() {
    AppTheme {
        BottomButtons()
    }
}