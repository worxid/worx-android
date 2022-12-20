package id.worx.device.client.screen.main

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import id.worx.device.client.R
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.PrimaryMainDark
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.util.DrawMode
import id.worx.device.client.util.MotionEvent
import id.worx.device.client.util.PathProperties
import id.worx.device.client.util.dragMotionEvent
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun SketchScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailFormViewModel,
    onBackNavigation: () -> Unit
) {
    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = "Draw Sketch",
                useProgressBar = false
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .background(MaterialTheme.colors.secondary)
                .fillMaxSize()
        ) {

            // Top Menu
            val isInTextMenu = remember {
                mutableStateOf(false)
            }
            val isInDrawMenu = remember {
                mutableStateOf(false)
            }

            // Brush Color
            val drawColor = remember {
                mutableStateOf(Color.Black)
            }
            val showColorPicker = remember {
                mutableStateOf(false)
            }

            // Brush Density
            val drawBrush = remember {
                mutableStateOf(5f)
            }
            val brushDensityLevel = remember {
                (5..15 step 5).toList()
            }

            // Canvas
            val paths = remember {
                mutableStateListOf<Pair<Path, PathProperties>>()
            }

            val pathsUndone = remember {
                mutableStateListOf<Pair<Path, PathProperties>>()
            }

            val currentPath = remember {
                mutableStateOf(Path())
            }

            val currentPathProperties = remember {
                mutableStateOf(PathProperties())
            }

            val motionEvent = remember {
                mutableStateOf(MotionEvent.Idle)
            }

            val currentPosition = remember {
                mutableStateOf(Offset.Unspecified)
            }

            val previousPosition = remember {
                mutableStateOf(Offset.Unspecified)
            }

            val drawMode = remember {
                mutableStateOf(DrawMode.Draw)
            }

            val allowDrawing = remember {
                mutableStateOf(true)
            }

            SketchTopMenu(
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .zIndex(99f),
                isInTextMenu = isInTextMenu,
                isInDrawMenu = isInDrawMenu,
                drawBrush = drawBrush,
                drawColor = drawColor,
                showColorPicker = showColorPicker,
                brushDensityLevel = brushDensityLevel,
                isPathNotEmpty = paths.isNotEmpty(),
                allowDrawing = allowDrawing,
                onDone = {
                    isInDrawMenu.value = false
                    isInTextMenu.value = false
                },
                onUndo = {
                    if (paths.isNotEmpty()) {
                        val lastItem = paths.last()
                        val lastPath = lastItem.first
                        val lastPathProperty = lastItem.second
                        paths.remove(lastItem)

                        pathsUndone.add(Pair(lastPath, lastPathProperty))
                    }
                }
            )

            SketchCanvasView(
                modifier = modifier.fillMaxSize(),
                paths = paths,
                pathsUndone = pathsUndone,
                currentPath = currentPath,
                motionEvent = motionEvent,
                currentPosition = currentPosition,
                previousPosition = previousPosition,
                drawMode = drawMode,
                allowDrawing = allowDrawing,
                drawBrush = drawBrush,
                drawColor = drawColor
            )

            if (!isInTextMenu.value && !isInDrawMenu.value) {
                SketchBottomMenu(
                    modifier = modifier.align(Alignment.BottomCenter),
                    inTextMenu = isInTextMenu,
                    inDrawMenu = isInDrawMenu
                )
            }

        }
    }
}

@Composable
private fun SketchCanvasView(
    modifier: Modifier,
    paths: SnapshotStateList<Pair<Path, PathProperties>>,
    pathsUndone: SnapshotStateList<Pair<Path, PathProperties>>,
    currentPath: MutableState<Path>,
    motionEvent: MutableState<MotionEvent>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    drawMode: MutableState<DrawMode>,
    allowDrawing: MutableState<Boolean>,
    drawBrush: MutableState<Float>,
    drawColor: MutableState<Color>
) {
    val context = LocalContext.current
    val drawModifier = modifier
        .background(Color.White)
        .dragMotionEvent(
            onDragStart = {
                if (allowDrawing.value){
                    motionEvent.value = MotionEvent.Down
                    currentPosition.value = it.position
                    it.consumeDownChange()
                }

            },
            onDrag = {
                if (allowDrawing.value){
                    motionEvent.value = MotionEvent.Move
                    currentPosition.value = it.position

                    if (drawMode.value == DrawMode.Touch) {
                        val change = it.positionChange()
                        paths.forEach { entry ->
                            val path = entry.first
                            path.translate(change)
                        }
                        currentPath.value.translate(change)
                    }
                    it.consumePositionChange()
                }

            },
            onDragEnd = {
                if (allowDrawing.value){
                    motionEvent.value = MotionEvent.Up
                    it.consumeDownChange()
                }
            }
        )

    Canvas(modifier = drawModifier) {
        if (allowDrawing.value){
            when (motionEvent.value) {
                MotionEvent.Down -> {
                    if (drawMode.value != DrawMode.Touch) {
                        currentPath.value.moveTo(currentPosition.value.x, currentPosition.value.y)
                    }
                    previousPosition.value = currentPosition.value
                }

                MotionEvent.Move -> {
                    if (drawMode.value != DrawMode.Touch) {
                        currentPath.value.quadraticBezierTo(
                            previousPosition.value.x,
                            previousPosition.value.y,
                            (previousPosition.value.x + currentPosition.value.x) / 2,
                            (previousPosition.value.y + currentPosition.value.y) / 2
                        )
                    }
                    previousPosition.value = currentPosition.value
                }

                MotionEvent.Up -> {
                    if (drawMode.value != DrawMode.Touch) {
                        currentPath.value.lineTo(currentPosition.value.x, currentPosition.value.y)

                        paths.add(Pair(currentPath.value, PathProperties(color = drawColor.value, stroke = drawBrush.value)))

                        currentPath.value = Path()

                    }
                    pathsUndone.clear()

                    currentPosition.value = Offset.Unspecified
                    previousPosition.value = currentPosition.value
                    motionEvent.value = MotionEvent.Idle
                }
                else -> Unit
            }
        }

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            paths.forEach {
                val path = it.first
                val property = it.second

                drawPath(
                    path = path,
                    color = property.color,
                    style = Stroke(property.stroke)
                )
            }

            if (motionEvent.value != MotionEvent.Idle) {
                drawPath(
                    path = currentPath.value,
                    color = drawColor.value,
                    style = Stroke(drawBrush.value)
                )
            }
            restoreToCount(checkPoint)
        }
    }
}


@Composable
private fun SketchTopMenu(
    modifier: Modifier,
    isInTextMenu: MutableState<Boolean>,
    isInDrawMenu: MutableState<Boolean>,
    showColorPicker: MutableState<Boolean>,
    brushDensityLevel: List<Int>,
    onDone: () -> Unit,
    onUndo: () -> Unit,
    drawBrush: MutableState<Float>,
    drawColor: MutableState<Color>,
    isPathNotEmpty: Boolean,
    allowDrawing: MutableState<Boolean>,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.54f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            if (isInDrawMenu.value || isInTextMenu.value) {
                TextButton(onClick = onDone) {
                    Text(
                        text = "DONE",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = fontRoboto,
                    )
                }

            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            if (!isInDrawMenu.value && !isInTextMenu.value) {
                allowDrawing.value = false
                ShowDefaultMenu(
                    onTextMenuClicked = {
                        isInTextMenu.value = true
                    },
                    onDrawMenuClicked = {
                        isInDrawMenu.value = true
                    }
                )
            } else if (isInTextMenu.value) {
                allowDrawing.value = false
                ShowTextMenu(
                    modifier = modifier,
                    showColorPicker = showColorPicker,
                    isPathNotEmpty = isPathNotEmpty,
                    onUndo = onUndo
                )
            } else if (isInDrawMenu.value) {
                allowDrawing.value = true
                ShowDrawMenu(
                    modifier = modifier,
                    brushDensityLevel = brushDensityLevel,
                    showColorPicker = showColorPicker,
                    drawBrush = drawBrush,
                    isPathNotEmpty = isPathNotEmpty,
                    onUndo = onUndo
                )
            }

        }
    }
}

@Composable
private fun ShowTextMenu(
    modifier: Modifier,
    showColorPicker: MutableState<Boolean>,
    isPathNotEmpty: Boolean,
    onUndo: () -> Unit
) {
    IconButton(onClick = onUndo, enabled = isPathNotEmpty) {
        Icon(
            imageVector = Icons.Default.Undo,
            contentDescription = "Undo",
            tint = if (isPathNotEmpty) Color.White else PrimaryMainDark
        )
    }
    Spacer(modifier = modifier.width(8.dp))
    ShowColorPicker(modifier = modifier, showColorPicker = showColorPicker)
}

@Composable
private fun ShowDrawMenu(
    modifier: Modifier,
    brushDensityLevel: List<Int>,
    showColorPicker: MutableState<Boolean>,
    onUndo: () -> Unit,
    drawBrush: MutableState<Float>,
    isPathNotEmpty: Boolean,
) {
    IconButton(onClick = onUndo, enabled = isPathNotEmpty) {
        Icon(
            imageVector = Icons.Default.Undo,
            contentDescription = "Undo",
            tint = if (isPathNotEmpty) Color.White else PrimaryMainDark,
        )
    }
    Spacer(modifier = modifier.width(8.dp))
    brushDensityLevel.forEach { level ->
        val selected = drawBrush.value.toInt() == level
        IconButton(
            modifier = modifier
                .clip(CircleShape)
                .background(color = if (selected) Color.Black.copy(0.54f) else Color.Transparent),
            onClick = {
                drawBrush.value = level.toFloat()
            }
        ) {
            Icon(
                painter = when (level) {
                    5 -> painterResource(id = R.drawable.ic_line_thin)
                    10 -> painterResource(id = R.drawable.ic_line_medium)
                    15 -> painterResource(id = R.drawable.ic_line_thick)
                    else -> painterResource(id = -1)
                },
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = modifier.width(8.dp))
    }
    ShowColorPicker(modifier = modifier, showColorPicker = showColorPicker)

}

@Composable
private fun ShowDefaultMenu(onTextMenuClicked: () -> Unit, onDrawMenuClicked: () -> Unit) {
    IconButton(
        onClick = onTextMenuClicked,
        modifier = Modifier
    ) {
        Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = Color.White)
    }

    IconButton(
        onClick = onDrawMenuClicked
    ) {
        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, tint = Color.White)
    }
}

@Composable
private fun SketchBottomMenu(
    modifier: Modifier,
    inTextMenu: MutableState<Boolean>,
    inDrawMenu: MutableState<Boolean>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.54f))
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.Collections,
                contentDescription = "Add Image",
                tint = Color.White,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "ADD IMAGE",
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = fontRoboto
            )
        }

        TextButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "DONE",
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = fontRoboto
            )
        }
    }
}

@Composable
private fun ShowColorPicker(modifier: Modifier, showColorPicker: MutableState<Boolean>) {
    FloatingActionButton(
        modifier = modifier.size(48.dp),
        onClick = {
            showColorPicker.value = true
        },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        backgroundColor = Color.Black
    ) {

    }
}


@Preview
@Composable
fun SketchScreenPreview() {
    WorxTheme() {
//        SketchScreen() {
//
//        }
    }
}