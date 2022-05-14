package com.example.pruebamaterial3.ui.list_screen.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pruebamaterial3.model.Todo
import com.example.pruebamaterial3.ui.list_screen.TodosUiEvent
import com.example.pruebamaterial3.ui.theme.AppFontTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItem(
    todo: Todo,
    modifier: Modifier = Modifier,
    onEvent: (TodosUiEvent) -> Unit,
) {
    var isDone by remember{ mutableStateOf(todo.isDone) }
    val textColorLight = if (isDone) Color.DarkGray else Color.White
    val textColorDark = if (isDone) Color.LightGray else Color.Black

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            modifier = Modifier.fillMaxWidth(.8f),
            text = todo.description,
            fontSize = 20.sp,
            fontFamily = AppFontTypography,
            color = if(isSystemInDarkTheme()) textColorLight else textColorDark
        )
        //Checkbox(
        //    checked = todo.isDone,
        //    onCheckedChange = {
        //        isDone = !isDone
        //        onEvent(TodosUiEvent.OnDoneChange(todo, isDone))
        //    }
        //)
        CircleCheckbox(
            selected = todo.isDone,
            onChecked = {
                isDone = !isDone
                onEvent(TodosUiEvent.OnDoneChange(todo, isDone))
            }
        )
    }
}