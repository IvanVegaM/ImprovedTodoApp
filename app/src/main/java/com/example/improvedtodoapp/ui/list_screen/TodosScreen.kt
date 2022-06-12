package com.example.improvedtodoapp.ui.list_screen

import android.app.DatePickerDialog
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.improvedtodoapp.R
import com.example.improvedtodoapp.model.Todo
import com.example.improvedtodoapp.model.classes.Days
import com.example.improvedtodoapp.model.classes.Keyboard
import com.example.improvedtodoapp.model.classes.Months
import com.example.improvedtodoapp.ui.list_screen.components.TodoItem
import com.example.improvedtodoapp.ui.theme.AppFontTypography
import com.example.improvedtodoapp.util.UiEvent
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TodosScreen(
    viewModel: TodosViewModel = hiltViewModel()
) {
    val todos = viewModel.todos.collectAsState(initial = emptyList())
    val state = viewModel.uiState.value
    val snackbarHostState = remember{ SnackbarHostState() }
    val scrollState = rememberLazyListState()
    val interactionSource = remember{ MutableInteractionSource() }

    //Pasar esto a viewmodel
    var deleteEnabled by remember{ mutableStateOf(false) }
    val context = LocalContext.current
    val isKeyboardOpen by keyboardAsState()
    val composableCoroutine = rememberCoroutineScope()

    val date = remember { mutableStateOf("") }

    /*val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerTheme,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date.value = "$dayOfMonth/${month+1}/$year"
        }, viewModel.year, viewModel.month, viewModel.dayMonth
    )*/

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(TodosUiEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if(state == TodosUiState.ListOnFocus &&
                isKeyboardOpen == Keyboard.Closed){
                FAB(
                    onClick = {
                        viewModel.onEvent(TodosUiEvent.OnAddTodo)
                        deleteEnabled = false
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            DateTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable{
                        viewModel.calendar.time = Date()
                        //datePickerDialog.show()
                    }
                    .padding(horizontal = 20.dp),
                dayWeek = Days.values()[viewModel.dayWeek - 1],
                dayMonth = viewModel.dayMonth,
                month = Months.values()[viewModel.month],
                year = viewModel.year
            )
        }
    ){ paddingValues ->
        val radius by animateDpAsState(
            if(state == TodosUiState.ListOnFocus){ 0.dp } else { 10.dp },
            animationSpec = tween(200),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = radius)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = scrollState,
                contentPadding = PaddingValues(bottom = 95.dp)
            ){
                items(
                    items = todos.value,
                    key = { todo ->
                        todo.id!!
                    }
                ){ todo ->
                    TodoItem(
                        todo = todo,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                viewModel.onEvent(TodosUiEvent.OnEditTodoClick(todo))
                                composableCoroutine.launch {
                                    if (!deleteEnabled) deleteEnabled = true
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            }
                            .padding(vertical = 14.dp, horizontal = 25.dp)
                    )
                }
            }
        }

        AddEditTodo(
            state = state,
            paddingValues = paddingValues,
            interactionSource = interactionSource,
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            deleteEnabled = deleteEnabled,
        )
    }
}

@Composable
fun DateTopBar(
    modifier:Modifier = Modifier,
    dayWeek: Days,
    dayMonth: Int,
    month: Months,
    year: Int
) {
    //val calendar = Calendar.getInstance()
    //val dayWeek = Days.values()[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    //val dayMonth = calendar.get(Calendar.DAY_OF_MONTH)
    //val month = Months.values()[calendar.get(Calendar.MONTH)]
    //val year = calendar.get(Calendar.YEAR)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            modifier = Modifier.fillMaxWidth(.27f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dayMonth.toString(),
                fontFamily = AppFontTypography,
                fontWeight = FontWeight.W400,
                fontSize = 55.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = month.toString(),
                    fontFamily = AppFontTypography,
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
                Text(
                    text = year.toString(),
                    fontFamily = AppFontTypography,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
                )
            }
        }
        Text(
            text = dayWeek.toString(),
            fontFamily = AppFontTypography,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        )
    }
}

@Composable
fun FAB( onClick: () -> Unit ) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.inversePrimary,
        modifier = Modifier
            .padding(bottom = 15.dp)
            .size(65.dp),
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add new todo",
            tint = Color.White
        )
    }
}

@Composable
fun AddEditTodo(
    state: TodosUiState,
    paddingValues: PaddingValues,
    interactionSource: MutableInteractionSource,
    viewModel: TodosViewModel,
    snackbarHostState: SnackbarHostState,
    deleteEnabled: Boolean,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Column {
        AnimatedVisibility(
            visible = state == TodosUiState.NewTodo ||
                    state == TodosUiState.EditTodo,
            enter = fadeIn(animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(100))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = 15.dp
                    )
                    .imePadding()
                    .clickable(interactionSource = interactionSource, indication = null) {
                        if (viewModel.description.isNotBlank()) {
                            viewModel.onEvent(
                                TodosUiEvent.OnSaveTodo(
                                    todo = Todo(
                                        description = viewModel.description,
                                        isDone = false
                                    )
                                )
                            )
                        }
                        viewModel.onEvent(TodosUiEvent.OnFocusList)
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(.7f)
                            .focusRequester(focusRequester),
                        value = viewModel.tfv, //viewModel.description
                        onValueChange = {
                            viewModel.onEvent(TodosUiEvent.OnDescriptionChange(it.text, it)) //viewModel.onEvent(TodosUiEvent.OnDescriptionChange(it))
                        },
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = AppFontTypography
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                        ),
                        placeholder = {
                            Text(
                                text = "New todo...",
                                fontSize = 20.sp,
                                fontFamily = AppFontTypography
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        maxLines = 3,
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Button(
                            onClick = {
                                viewModel.onEvent(
                                    TodosUiEvent.OnSaveTodo(
                                        todo = Todo(
                                            description = viewModel.description,
                                            isDone = false
                                        )
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = viewModel.description.isNotEmpty()
                        ) {
                            Text(
                                text = "Save",
                                fontFamily = AppFontTypography
                            )
                        }

                        if(deleteEnabled){
                            Spacer(modifier = Modifier.width(20.dp))

                            Button(
                                onClick = {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    viewModel.onEvent(TodosUiEvent.OnDeleteTodoClick(viewModel.todo!!))
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.inversePrimary
                                ),
                                //enabled = deleteEnabled
                            ) {
                                Text(
                                    text = "Delete",
                                    fontFamily = AppFontTypography
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

@Composable
fun Calendar() {
    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

}