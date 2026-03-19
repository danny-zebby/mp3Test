package com.example.temp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.primaryBGLight
import com.example.compose.secondaryBGLight
import com.example.temp.ui.theme.NewTheme
import com.kavi.droid.color.picker.ui.KvColorPickerBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPick(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
){
    // Create state variable to show and hide bottom sheet
    val showSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    Column(modifier = Modifier.fillMaxSize().background(primaryBGLight))
    {
        Box(modifier = Modifier.height(50.dp).fillMaxWidth().background(secondaryBGLight)) { }
        TopBar(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(10.dp))


        // Button click to open bottom-sheet
        Button(
            onClick = {
                showSheet.value = true
            }
        ) {
            Text("Open Color Picker")
        }

        // Color Picker bottom sheet UI
        if (showSheet.value) {
            KvColorPickerBottomSheet(
                showSheet = showSheet,
                sheetState = sheetState,
                onColorSelected = { selectedColor ->
                    // Do anything when you have selected color
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun tempreview() {
    NewTheme {
        ColorPick()
    }
}