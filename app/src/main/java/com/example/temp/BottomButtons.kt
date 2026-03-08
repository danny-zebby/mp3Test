import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.RectangleShape
import com.example.temp.ui.theme.NewTheme


@Composable
fun BottomButtons(modifier: Modifier = Modifier) {
    var toggle by remember { mutableStateOf(true) }
    var activeIndex by remember { mutableStateOf(-1) } // currently highlighted button
    var dragging by remember { mutableStateOf(false) }
    var lastAction by remember { mutableStateOf("None") }

    val symbols1 = listOf("C>", "||", ">", ">>")
    val symbols2 = listOf("O>", "||", "<", "<<")

    // Row for 5 buttons
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Only start dragging if the first button is pressed
                        val buttonWidth = size.width / 5
                        if (offset.x <= buttonWidth) {
                            dragging = true
                            activeIndex = -1
                        }
                    },
                    onDrag = { change, _ ->
                        if (!dragging) return@detectDragGestures
                        change.consume()
                        val buttonWidth = size.width / 5
                        //check first index
                        if (change.position.x <= buttonWidth) {
                            activeIndex = -1  // do nothing when over first button
                            return@detectDragGestures
                        }
                        // Track finger position across buttons 2–5
                        val index = ((change.position.x - buttonWidth) / buttonWidth).toInt()
                        activeIndex = index.coerceIn(0, 3)
                    },
                    onDragEnd = {
                        if (activeIndex >= 0) {
                            lastAction = "Button ${activeIndex + 2} clicked"
                            println(lastAction)
                        } else {
                            lastAction = "Drag cancelled"  // drag ended back on first button
                            println(lastAction)
                        }
                        dragging = false
                        activeIndex = -1
                        toggle = !toggle // toggle first button on release
                    },
                    onDragCancel = {
                        dragging = false
                        activeIndex = -1
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val controlModifier = Modifier.weight(1f).height(100.dp)

        // First button (toggle + drag)
        Button(
            onClick = { toggle = !toggle }, // normal click fallback
            shape = RectangleShape,
            modifier = controlModifier,
            colors = ButtonDefaults.buttonColors(containerColor = if (!dragging) Color(0xFF196D8A) else Color(0xFF145e77))
        ) {
            Text(text = if (toggle) "^" else "V")
        }

        // Remaining 4 buttons
        val symbols = if (toggle) symbols1 else symbols2
        symbols.forEachIndexed { index, symbol ->
            val bg = if (dragging && activeIndex == index) Color(0xFF145e77) else Color(0xFF196D8A)
            Button(
                onClick = { println("Button ${index + 2} clicked") },
                shape = RectangleShape,
                modifier = controlModifier,
                colors = ButtonDefaults.buttonColors(containerColor = bg)
            ) {
                Text(text = symbol)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomButtonsPreview() {
    NewTheme {
        BottomButtons()
    }
}