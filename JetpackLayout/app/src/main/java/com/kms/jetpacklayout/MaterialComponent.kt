package com.kms.jetpacklayout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kms.jetpacklayout.ui.theme.JetpackLayoutTheme


// 사전 정의된 Material 아이콘
// https://fonts.google.com/icons?selected=Material+Icons
@Composable
fun MaterialScreen(){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Material Component")
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Rounded.Favorite, contentDescription = null)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Rounded.Menu, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  }){
                Icon(Icons.Rounded.Add , contentDescription = null)
            }
        }
    ) { innerPadding ->
        BodyContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier){
    Column(modifier = modifier) {
        Text(text = "Hi there!")
        Text(text = "Thanks for going through the Layouts codelab")
    }
}

@Preview
@Composable
fun MaterialPreview(){
    JetpackLayoutTheme {
        MaterialScreen()
    }
}
