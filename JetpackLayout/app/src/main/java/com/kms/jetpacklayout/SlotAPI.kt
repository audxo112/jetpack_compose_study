package com.kms.jetpacklayout

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kms.jetpacklayout.ui.theme.JetpackLayoutTheme


// content lambda 를 통해 Button UI를 유동적으로 변경한다
@Composable
fun SlotAPI(){
    Button(onClick = {  }) {
        Row{
            Icon(Icons.Rounded.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "버튼")
        }
    }
}


@Preview
@Composable
fun SlotAPIPreview(){
    JetpackLayoutTheme {
        SlotAPI()
    }
}