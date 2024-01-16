package com.example.todocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todocompose.ui.theme.TodocomposeTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodocomposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TodoScreen() {
    var todoList by remember { mutableStateOf(listOf<TodoItem>()) }
    var newTodo by remember { mutableStateOf("") }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Todo List",
                fontSize = 24.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Todo",
                modifier = Modifier
                    .clickable {
                        newTodo = ""
                        editingTodo = null
                    }
                    .size(32.dp)
            )
        }

        TodoList(todoList = todoList, onTodoClick = {
            // Handle Todo item click
            newTodo = it.task
            editingTodo = it
        })

        TodoInput(
            text = newTodo,
            onTextChange = { newTodo = it },
            onSendClick = {
                if (newTodo.isNotEmpty()) {
                    if (editingTodo != null) {
                        // Edit existing Todo
                        todoList = todoList.map {
                            if (it == editingTodo) {
                                it.copy(task = newTodo)
                            } else {
                                it
                            }
                        }
                        newTodo = ""
                        editingTodo = null
                    } else {
                        // Add new Todo
                        todoList = todoList + TodoItem(todoList.size + 1, newTodo)
                        newTodo = ""
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TodoInput(text: String, onTextChange: (String) -> Unit, onSendClick: () -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val inputInteractionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                onTextChange(it)
                isEditing = it.isNotEmpty()
            },
            label = { Text("New Todo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSendClick()
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                .padding(8.dp),
            interactionSource = inputInteractionSource
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditing) {
                IconButton(
                    onClick = {
                        onTextChange("")
                        isEditing = false
                        keyboardController?.hide()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                }
            }

            IconButton(
                onClick = {
                    onSendClick()
                    isEditing = false
                    keyboardController?.hide()
                }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}


data class TodoItem(val id: Int, val task: String)

@Composable
fun TodoItem(todo: TodoItem, onTodoClick: (TodoItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = todo.task, fontSize = 18.sp)
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Todo")
    }
}

@Composable
fun TodoList(todoList: List<TodoItem>, onTodoClick: (TodoItem) -> Unit) {
    LazyColumn {
        items(todoList) { todo ->
            TodoItem(todo = todo, onTodoClick = onTodoClick)
        }
    }
}
