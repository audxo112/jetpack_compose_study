package com.kms.parallaxeffect

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParallaxScreen(modifier = Modifier
                .fillMaxSize()
            )
        }
    }
}

@Composable
fun ParallaxScreen(modifier:Modifier = Modifier){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var data by remember { mutableStateOf<SensorData?>(null) }

    DisposableEffect(Unit){
        val dataManager = SensorDataManager(context)
        val job = scope.launch {
            dataManager.data.receiveAsFlow().onEach {
                data = it
            }.collect()
        }

        onDispose {
            dataManager.cancel()
            job.cancel()
        }
    }

    Box(modifier = modifier){
        ParallaxView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            multiplier = 20,
            data = data
        )
    }
}

@Composable
fun ParallaxView(
    modifier:Modifier = Modifier,
    multiplier: Int = 20,
    data:SensorData?
){
    val roll by derivedStateOf { (data?.roll ?: 0f) * multiplier }
    val pitch by derivedStateOf { (data?.pitch ?: 0f) * multiplier }

    Box(modifier = modifier){
        Image(
            painter = painterResource(R.drawable.parallax_img),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = - (roll * 1.5).dp.roundToPx(),
                        y = (pitch * 2).dp.roundToPx()
                    )
                }
                .size(width = 256.dp, height = 356.dp)
                .align(Alignment.Center)
                .blur(radius = 200.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .offset{
                    IntOffset(
                        x = (roll * 0.9).dp.roundToPx(),
                        y = -(pitch * 0.9).dp.roundToPx()
                    )
                }
                .size(width = 300.dp, height = 400.dp)
                .align(Alignment.Center)
                .background(
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
        )

        Image(
            painter = painterResource(R.drawable.parallax_img),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = roll.dp.roundToPx(),
                        y = -pitch.dp.roundToPx()
                    )
                }
                .size(width = 300.dp, height = 400.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(
                horizontalBias = (roll * 0.005).toFloat(),
                verticalBias = 0f,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ParallaxScreen()
}