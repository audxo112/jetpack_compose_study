package com.kms.jetpackstate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kms.jetpackstate.ui.theme.JetpackStateTheme
import kotlin.random.Random

@Composable
fun TodoScreen(
    items: List<TodoItem>,
    currentlyEditing: TodoItem?,
    onAddItem: (TodoItem) -> Unit,
    onRemoveItem: (TodoItem) -> Unit,
    onStartEdit: (TodoItem) -> Unit,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit
){
    Column {
        val enableTopSection = currentlyEditing == null
        if (enableTopSection){
            TodoItemEntryInput(onItemComplete = onAddItem)
        }
        else{
            Text(
                text = "Editing item",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(items){ todo ->
                if (currentlyEditing != null && currentlyEditing.id == todo.id){
                    TodoItemInlineEditor(
                        item = currentlyEditing,
                        onEditItemChange = onEditItemChange,
                        onEditDone = onEditDone,
                        onRemoveItem = { onRemoveItem(todo) }
                    )
                }
                else{
                    TodoRow(
                        todo = todo,
                        onItemClicked = onStartEdit,
                        modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

fun randomTint() = Random.nextInt(3, 10).toFloat() / 10

@Composable
fun TodoRow(
    todo: TodoItem,
    onItemClicked:(TodoItem) -> Unit,
    modifier:Modifier = Modifier,
    iconAlpha: Float = remember(todo.id){ randomTint() }
){
    Row(
        modifier = modifier
            .clickable { onItemClicked(todo) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = todo.task)
        Icon(
            imageVector = Icons.Filled.Add,
            tint = LocalContentColor.current.copy(alpha = iconAlpha),
            contentDescription = null
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TodoInputTextField(text:String, onTextChange: (String) -> Unit, modifier:Modifier = Modifier, onImeAction:()->Unit = {}){
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
                keyboardController?.hide()
            }
        ),
    )
}

@Composable
fun TodoItemInlineEditor(
    item: TodoItem,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
    onRemoveItem: () -> Unit
) = TodoItemInput(
    text = item.task,
    onTextChange = {onEditItemChange(item.copy(task = it))},
    submit = onEditDone,
    iconVisible = true,
    buttonSlot = {
        Row {
            val shrinkButtons = Modifier.widthIn(20.dp)
            TextButton(onClick = onEditDone, modifier = shrinkButtons) {
                Text(
                    text = "\uD83D\uDCBE",
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(30.dp)
                )
            }
            TextButton(onClick = onRemoveItem, modifier = shrinkButtons) {
                Text(
                    text = "❌",
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(30.dp)
                )
            }
        }
    }
)


@Composable
fun TodoItemEntryInput(onItemComplete: (TodoItem) -> Unit){
    val (text, setText) = remember { mutableStateOf("") }
    val iconVisible = text.isNotBlank()
    val submit = {
        onItemComplete(TodoItem(text))
        setText("")
    }

    TodoItemInput(text = text, onTextChange = setText, submit = submit, iconVisible = iconVisible){
        Button(
            onClick = submit,
            enabled = iconVisible
        ){
            Text(text = "Add")
        }
    }
}


@Composable
fun TodoItemInput(text:String, onTextChange: (String) -> Unit, submit:()->Unit, iconVisible:Boolean, buttonSlot: @Composable () -> Unit){
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            TodoInputTextField(
                text = text,
                onTextChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
                onImeAction = submit
            )

            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.align(Alignment.CenterVertically)){
                buttonSlot()
            }
        }

        if(iconVisible){

        }
        else{
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun TodoPreview(){
    val todoViewModel = TodoViewModel()
    JetpackStateTheme {
        TodoScreen(
            items = todoViewModel.todoItems,
            currentlyEditing = todoViewModel.currentEditItem,
            onAddItem = { todoViewModel.addItem(it) },
            onRemoveItem = { todoViewModel.removeItem(it) },
            onStartEdit = { todoViewModel.onEditItemSelected(it) },
            onEditItemChange = { todoViewModel.onEditItemChange(it) },
            onEditDone = { todoViewModel.onEditDone() }
        )
    }
}