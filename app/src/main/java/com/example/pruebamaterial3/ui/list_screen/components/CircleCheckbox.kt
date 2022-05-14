package com.example.pruebamaterial3.ui.list_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CircleCheckbox(selected: Boolean, enabled: Boolean = true, onChecked: () -> Unit) {

    val color = MaterialTheme.colorScheme
    val imageVector = if (selected) Icons.Rounded.Check else Icons.Outlined.Circle
    val background = if (selected) color.primary else Color.Transparent
    val tint = if (selected) Color.White else Color.Black

    IconButton(
        onClick = onChecked,
        enabled = enabled
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            color = background,
            shape = CircleShape
        ){
            Icon(
                imageVector = imageVector,
                tint = tint,
                modifier = Modifier.size(28.dp),
                contentDescription = "checkbox"
            )
        }
    }
}
