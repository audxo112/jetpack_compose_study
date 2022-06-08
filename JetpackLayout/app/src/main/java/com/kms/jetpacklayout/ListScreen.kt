package com.kms.jetpacklayout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kms.jetpacklayout.ui.theme.JetpackLayoutTheme
import kotlinx.coroutines.launch


@Composable
fun ListScreen(){
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(100){
            Text("Item $it")
        }
    }
}

@Composable
fun ImageListScreen(modifier: Modifier = Modifier.fillMaxWidth()){
    val listSize = 100
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Column {
        Row{
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text(text="맨 위로")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }) {
                Text(text="맨 아래로")
            }
        }
        LazyColumn(state = scrollState) {
            items(listSize){
                ImageListItem(index = it)
            }
        }
    }
}

@Composable
fun ImageListItem(index:Int){
    Row(verticalAlignment = Alignment.CenterVertically){
        AsyncImage(
            model = "https://developer.android.com/images/brand/Android_Robot.png",
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(text = "Item $index", style = MaterialTheme.typography.subtitle1)
    }
}


@Preview
@Composable
fun ListPreview(){
    JetpackLayoutTheme {
        ImageListScreen()
    }
}