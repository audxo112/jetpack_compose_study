package com.kms.jetpacklayout

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kms.jetpacklayout.ui.theme.JetpackLayoutTheme


// 하위 요소는 한 번만 측정할 수 있다
fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout{ // 측정하고 배치할 하위 요소
            measurable,
            // 하위 요소의 너비와 높이 최소값과 최댓값
            constraints ->
        val placeable = measurable.measure(constraints)
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height){
            placeable.placeRelative(0, placeableY)
        }
    }
)


@Composable
fun CustomLayout(
    modifier: Modifier = Modifier,
    content: @Composable ()->Unit
){
    Layout(
        modifier = modifier,
        content = content
    ){ measurables, constraints ->
        val placeables = measurables.map{ measurable ->
            measurable.measure(constraints)
        }
        var yPosition = 0

        layout(constraints.maxWidth, constraints.maxHeight){
            placeables.forEach{ placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }

    }
}


@Composable
fun CustomBodyContent(modifier: Modifier = Modifier){
    CustomLayout(Modifier.padding(8.dp)) {
        Text(text = "Text1")
        Text(text = "Text2")
        Text(text = "Text3")
        Text(text = "Text4")
    }
}


@Preview
@Composable
fun CustomLayoutPreview(){
    CustomBodyContent()
}


@Preview
@Composable
fun TextWithPaddingToBaselinePreview(){
    JetpackLayoutTheme {
        Text(text = "Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}


@Preview
@Composable
fun TextWithNormalPaddingPreview(){
    JetpackLayoutTheme {
        Text(text = "Hi there!", Modifier.padding(top = 32.dp))
    }
}