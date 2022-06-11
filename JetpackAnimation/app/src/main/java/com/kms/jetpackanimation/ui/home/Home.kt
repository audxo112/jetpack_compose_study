package com.kms.jetpackanimation.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kms.jetpackanimation.R
import com.kms.jetpackanimation.ui.theme.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


private enum class TabPage{
    Home, Work
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(){
    val allTasks = stringArrayResource(id = R.array.tasks)
    val allTopics = stringArrayResource(id = R.array.topics)

    var tabPage by remember { mutableStateOf(TabPage.Home) }
    var weatherLoading by remember { mutableStateOf(false) }

    val tasks = remember { mutableStateListOf<String>() }
    var expandedTopic by remember { mutableStateOf<String?>(null) }
    var editMessageShown by remember { mutableStateOf(false) }

    suspend fun loadWeather(){
        if(!weatherLoading){
            weatherLoading = true
            delay(3000)
            weatherLoading = false
        }
    }

    suspend fun showEditMessage(){
        if(!editMessageShown){
            editMessageShown = true
            delay(3000)
            editMessageShown = false
        }
    }

    LaunchedEffect(Unit){
        loadWeather()
    }

    val lazyListState = rememberLazyListState()
    val backgroundColor by animateColorAsState(if (tabPage == TabPage.Home) Purple80 else Green300)
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HomeTabBar(
                backgroundColor = backgroundColor,
                tabPage = tabPage,
                onTabSelected = { tabPage = it }
            )
        },
        containerColor = backgroundColor,
        floatingActionButton = {
            HomeFloatingActionButton(
                extended = lazyListState.isScrollingUp(),
                onClick = {
                    coroutineScope.launch {
                        showEditMessage()
                    }
                }
            )
        }
    ){
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
            contentPadding = it,
            state = lazyListState
        ){
            item { Header(title = stringResource(id = R.string.weather)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp
                ) {
                    if(weatherLoading){
                        LoadingRow()
                    } else{
                        WeatherRow(onRefresh = {
                            coroutineScope.launch {
                                loadWeather()
                            }
                        })
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item { Header(title = stringResource(id = R.string.topics)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            items(allTopics){ topic ->
                TopicRow(
                    topic = topic,
                    expanded = expandedTopic == topic,
                    onClick = {
                        expandedTopic = if (expandedTopic == topic) null else topic
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item { Header(title = stringResource(id = R.string.tasks)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            if (tasks.isEmpty()){
                item {
                    TextButton(
                        onClick = {
                            tasks.clear()
                            tasks.addAll(allTasks)
                        }
                    ) {
                        Text(stringResource(id = R.string.add_tasks))
                    }
                }
            }
            items(
                items = tasks,
                key = { it }
            ) { task ->
                TaskRow(
                    task = task,
                    onRemove = { tasks.remove(task) }
                )
            }
        }
        EditMessage(
            modifier = Modifier.padding(it),
            shown = editMessageShown
        )
    }
}


@Composable
private fun HomeTabBar(
    backgroundColor: Color,
    tabPage: TabPage,
    onTabSelected: (tabPage: TabPage) -> Unit
){
    TabRow(
        selectedTabIndex = tabPage.ordinal,
        containerColor = backgroundColor,
        indicator = { tabPositions ->
            HomTabIndicator(tabPosition = tabPositions, tabPage = tabPage)
        },
        divider = {}
    ) {
        HomeTab(
            icon = Icons.Default.Home,
            title = stringResource(id = R.string.home),
            onClick = { onTabSelected(TabPage.Home) }
        )
        HomeTab(
            icon = Icons.Default.AccountBox,
            title = stringResource(id = R.string.work),
            onClick = { onTabSelected(TabPage.Work) }
        )
    }
}


@Composable
private fun HomTabIndicator(
    tabPosition: List<TabPosition>,
    tabPage: TabPage
){
    val transition = updateTransition(
        tabPage,
        label = "Tab indicator"
    )

    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work){
                spring(stiffness = Spring.StiffnessVeryLow)
            }
            else{
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { page ->
        tabPosition[page.ordinal].left
    }

    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work){
                spring(stiffness = Spring.StiffnessMedium)
            }
            else{
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator Right"
    ) { page ->
        tabPosition[page.ordinal].right
    }

    val color by transition.animateColor(
        label = "Border color"
    ){ page ->
       if (page == TabPage.Home) Purple40 else Green800
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(2.dp, color),
                RoundedCornerShape(4.dp)
            )
    )
}


@Composable
private fun HomeTab(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}


@Composable
private fun HomeFloatingActionButton(
    extended: Boolean,
    onClick: () -> Unit
){
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ){
            Icon(
                imageVector = Icons.Default.Edit, 
                contentDescription = null
            )
            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(id = R.string.edit),
                    modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}


@Composable
private fun Header(
    title: String
){
    Text(
        text = title,
        modifier = Modifier.semantics {
            heading()
        },
        style = MaterialTheme.typography.headlineSmall
    )
}


@Composable
private fun LoadingRow(){
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    Color.LightGray.copy(
                        alpha = alpha
                    )
                )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(
                    Color.LightGray.copy(
                        alpha = alpha
                    )
                )
        )
    }
}


@Composable
private fun WeatherRow(
    onRefresh: () -> Unit
){
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Amber600)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.weather_temperature),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onRefresh){
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(id = R.string.weather_refresh)
            )
        }
    }
}


@Composable
private fun TopicRowSpacer(visible: Boolean){
    AnimatedVisibility(visible = visible) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun TopicRow(topic:String, expanded: Boolean, onClick: () -> Unit){
    TopicRowSpacer(visible = expanded)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tonalElevation = 2.dp,
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row{
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = topic,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (expanded){
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.lorem_ipsum),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
    TopicRowSpacer(visible = expanded)
}


@Composable
private fun TaskRow(task: String, onRemove: () -> Unit){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .swipeToDismiss(onRemove),
        tonalElevation = 2.dp
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = task,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Composable
private fun EditMessage(
    modifier: Modifier = Modifier,
    shown:Boolean,
){
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically (
            targetOffsetY = {fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
            tonalElevation = 4.dp
        ) {
            Text(
                text = stringResource(id = R.string.edit_message),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
private fun LazyListState.isScrollingUp() : Boolean{
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex){
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also{
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


private fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit
) : Modifier = composed{
    val offsetX = remember { Animatable(0f) }
    pointerInput(Unit){
        val decay = splineBasedDecay<Float>(this)
        coroutineScope {
            while(true){
                val pointerId = awaitPointerEventScope {
                    awaitFirstDown().id
                }
                offsetX.stop()
                val velocityTracker = VelocityTracker()
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        launch {
                            offsetX.snapTo(horizontalDragOffset)
                        }
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        if (change.positionChange() != Offset.Zero) {
                            change.consume()
                        }
                    }
                }
                val velocity = velocityTracker.calculateVelocity().x
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch { 
                    if (targetOffsetX.absoluteValue <= size.width){
                        offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else{
                        offsetX.animateDecay(velocity, decay)
                        onDismissed()
                    }
                }
            }
        }
    }.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}

@Preview
@Composable
private fun PreviewHomeTabBar() {
    HomeTabBar(
        backgroundColor = Purple80,
        tabPage = TabPage.Home,
        onTabSelected = {}
    )
}

@Preview
@Composable
private fun PreviewHome() {
    JetpackAnimationTheme {
        Home()
    }
}
