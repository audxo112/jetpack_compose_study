package com.kms.jetpacklayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kms.jetpacklayout.ui.theme.JetpackLayoutTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackLayoutTheme {
                ChipBodyContent()
            }
        }
    }
}


