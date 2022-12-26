package id.worx.device.client.screen.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.CoilAdapter
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import id.worx.device.client.R
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.screen.components.getActivity
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.PrimaryMainDark
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.util.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import kotlin.math.roundToInt

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
            val context = LocalContext.current

            val state = viewModel.uiState.collectAsState()

            // Menu
            val currentMenu = remember {
                mutableStateOf(Menus.Default)
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

            // Draw Canvas
            val paths = remember {
                mutableStateListOf<Pair<Path, PathProperties>>()
            }

            val pathsUndone = remember {
                mutableStateListOf<Pair<Path, PathProperties>>()
            }

            val currentPath = remember {
                mutableStateOf(Path())
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

            // Image canvas
            var imageCanvasBitmap by remember {
                mutableStateOf<Bitmap?>(null)
            }

            // Text canvas
            val texts = remember {
                mutableStateListOf<Pair<String, TextProperties>>()
            }
            val textCanvas = remember {
                mutableStateOf("")
            }
            val showTextFieldOnCanvas = remember {
                mutableStateOf(false)
            }
            val drawTextColor = remember {
                mutableStateOf(Color.Black)
            }

            // capture canvas
            val captureController = rememberCaptureController()
            val capturedBitmap = remember {
                mutableStateOf<Bitmap?>(null)
            }

            val launcherGallery = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    if (it.resultCode == Activity.RESULT_OK &&
                        it.data != null
                    ) {
                        it.data!!.getParcelableArrayListExtra<Uri>(FishBun.INTENT_PATH)
                            ?.forEach { uri ->
                                val bitmap = uri.toBitmap(context)
                                imageCanvasBitmap = bitmap
                            }
                    }
                })

            SketchTopMenu(
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .zIndex(99f),
                currentMenu = currentMenu,
                drawBrush = drawBrush,
                drawColor = drawColor,
                drawTextColor = drawTextColor,
                showColorPicker = showColorPicker,
                isPathNotEmpty = paths.isNotEmpty(),
                allowDrawing = allowDrawing,
                showTextFieldOnCanvas = showTextFieldOnCanvas,
                onDone = {
                    currentMenu.value = Menus.Default
                    if (paths.isNotEmpty()) {
                        captureController.capture()
                    }
                    showTextFieldOnCanvas.value = false
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

            Capturable(
                modifier = modifier.fillMaxSize(),
                controller = captureController,
                onCaptured = { imageBitmap, error ->
                    if (imageBitmap != null) {
                        capturedBitmap.value = imageBitmap.asAndroidBitmap()
                    }
                    if (error != null) {
                        Log.e("WORX", "Sketch capture error: ${error.message}")
                    }
                }) {
                SketchCanvasView(
                    modifier = modifier.fillMaxSize(),
                    paths = paths,
                    texts = texts,
                    pathsUndone = pathsUndone,
                    currentPath = currentPath,
                    motionEvent = motionEvent,
                    currentPosition = currentPosition,
                    previousPosition = previousPosition,
                    drawMode = drawMode,
                    allowDrawing = allowDrawing,
                    drawBrush = drawBrush,
                    drawColor = drawColor,
                    drawTextColor = drawTextColor,
                    imageCanvas = imageCanvasBitmap,
                    textCanvas = textCanvas,
                    showTextFieldOnCanvas = showTextFieldOnCanvas,
                    currentMenu = currentMenu
                )
            }

            if (currentMenu.value == Menus.Default) {
                SketchBottomMenu(
                    modifier = modifier.align(Alignment.BottomCenter),
                    context = context,
                    launcher = launcherGallery,
                    onDone = {
                        if (capturedBitmap.value != null) {
                            viewModel.saveSketch(
                                capturedBitmap.value!!,
                                viewModel.uiState.value.currentComponent
                            )
                        }
                    }
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
    drawColor: MutableState<Color>,
    imageCanvas: Bitmap?,
    showTextFieldOnCanvas: MutableState<Boolean>,
    textCanvas: MutableState<String>,
    drawTextColor: MutableState<Color>,
    texts: SnapshotStateList<Pair<String, TextProperties>>,
    currentMenu: MutableState<Menus>
) {
    val context = LocalContext.current

    val drawModifier = modifier
        .background(Color.White)
        .dragMotionEvent(
            onDragStart = {
                if (allowDrawing.value) {
                    motionEvent.value = MotionEvent.Down
                    currentPosition.value = it.position
                    if (it.pressed != it.previousPressed) it.consume()
                }
//                if (currentMenu.value == Menus.Text) {
//                    showTextFieldOnCanvas.value = !showTextFieldOnCanvas.value
//                }
            },
            onDrag = {
                if (allowDrawing.value) {
                    motionEvent.value = MotionEvent.Move
                    currentPosition.value = it.position

//                    if (currentMenu.value == Menus.Text) {
//                        val change = it.positionChange()
//                        paths.forEach { entry ->
//                            val path = entry.first
//                            path.translate(change)
//                        }
//                        currentPath.value.translate(change)
//                    }
                    if (it.positionChange() != Offset.Zero) it.consume()
                }

            },
            onDragEnd = {
                if (allowDrawing.value) {
                    motionEvent.value = MotionEvent.Up
                    if (it.pressed != it.previousPressed) it.consume()
                }
            }
        )

    // data offsetnya jg sepertinya bisa disatukan dengan strint & property textnya,
    // bisa dibikin ke satu data class baru
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var offsetX2 by remember { mutableStateOf(0f) }
    var offsetY2 by remember { mutableStateOf(0f) }

    var offsetX3 by remember { mutableStateOf(0f) }
    var offsetY3 by remember { mutableStateOf(0f) }

    if (showTextFieldOnCanvas.value) {
        val focusRequester = remember {
            FocusRequester()
        }

        val tempText = remember {
            mutableStateOf("")
        }

        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.54f))
                .zIndex(2f)
                .pointerInput(Unit) {
                    detectTapGestures {
                        showTextFieldOnCanvas.value = false
                        textCanvas.value = tempText.value
                        texts.add(
                            Pair(
                                tempText.value,
                                TextProperties(
                                    color = drawTextColor.value
                                )
                            )
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = tempText.value,
                onValueChange = {
                    tempText.value = it
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
            )
        }
    }

    Box() {
        Canvas(modifier = drawModifier) {
            if (allowDrawing.value) {
                when (motionEvent.value) {
                    MotionEvent.Down -> {
                        if (drawMode.value != DrawMode.Touch) {
                            currentPath.value.moveTo(
                                currentPosition.value.x,
                                currentPosition.value.y
                            )
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
                            currentPath.value.lineTo(
                                currentPosition.value.x,
                                currentPosition.value.y
                            )

                            paths.add(
                                Pair(
                                    currentPath.value,
                                    PathProperties(
                                        color = drawColor.value,
                                        stroke = drawBrush.value
                                    )
                                )
                            )

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

                imageCanvas?.let {
                    drawImage(
                        imageCanvas.asImageBitmap(), topLeft = Offset(
                            x = (size.width - it.width) / 2f,
                            y = (size.height - it.height) / 2f
                        )
                    )
                }

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

//            if (textCanvas.value.isNotBlank()) {
//                val paint = android.graphics.Paint().apply {
//                    textAlign = android.graphics.Paint.Align.CENTER
//                    textSize = 64f
//                    color = drawTextColor.value.toArgb()
//                }
//                drawText(textCanvas.value, center.x, center.y, paint)
//            }
//                texts.forEach {
//                    val text = it.first
//                    val textProperties = it.second
//
//                    val paint = android.graphics.Paint().apply {
//                        textAlign = android.graphics.Paint.Align.CENTER
//                        textSize = 64f
//                        color = textProperties.color.toArgb()
//                    }
//                    drawText(text, center.x, center.y, paint)
//                }
                restoreToCount(checkPoint)
            }
        }
    }
    // Ini ntar bisa diganti dengan lazy column utk optimasissasi codenya
    Column{
            Text(
                modifier = Modifier
                    .padding(96.dp)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    },
                text = "Text1",
                style = TextStyle(color = Color.Black)
            )
        Text(
            modifier = Modifier
                .padding(12.dp)
                .offset { IntOffset(offsetX2.roundToInt(), offsetY2.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX2 += dragAmount.x
                        offsetY2 += dragAmount.y
                    }
                },
            text = "Text2",
            style = TextStyle(color = Color.Black)
        )
        Text(
            modifier = Modifier
                .padding(12.dp)
                .offset { IntOffset(offsetX3.roundToInt(), offsetY3.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX3 += dragAmount.x
                        offsetY3 += dragAmount.y
                    }
                },
            text = "Text3",
            style = TextStyle(color = Color.Black)
        )
    }
}


@Composable
private fun SketchTopMenu(
    modifier: Modifier,
    showColorPicker: MutableState<Boolean>,
    onDone: () -> Unit,
    onUndo: () -> Unit,
    drawBrush: MutableState<Float>,
    drawColor: MutableState<Color>,
    isPathNotEmpty: Boolean,
    allowDrawing: MutableState<Boolean>,
    showTextFieldOnCanvas: MutableState<Boolean>,
    drawTextColor: MutableState<Color>,
    currentMenu: MutableState<Menus>,
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
            if (currentMenu.value != Menus.Default) {
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
            when (currentMenu.value) {
                Menus.Default -> {
                    allowDrawing.value = false
                    showTextFieldOnCanvas.value = false
                    ShowDefaultMenu(
                        onTextMenuClicked = {
                            currentMenu.value = Menus.Text
                        },
                        onDrawMenuClicked = {
                            currentMenu.value = Menus.Draw
                        }
                    )
                }
                Menus.Text -> {
                    allowDrawing.value = false
                    showTextFieldOnCanvas.value = true
                    ShowTextMenu(
                        modifier = modifier,
                        showColorPicker = showColorPicker,
                        isPathNotEmpty = isPathNotEmpty,
                        drawTextColor = drawTextColor,
                        onUndo = onUndo
                    )
                }
                Menus.Draw -> {
                    allowDrawing.value = true
                    showTextFieldOnCanvas.value = false
                    ShowDrawMenu(
                        modifier = modifier,
                        showColorPicker = showColorPicker,
                        drawBrush = drawBrush,
                        drawColor = drawColor,
                        isPathNotEmpty = isPathNotEmpty,
                        onUndo = onUndo
                    )
                }
            }

        }
    }
}

@Composable
private fun ShowTextMenu(
    modifier: Modifier,
    showColorPicker: MutableState<Boolean>,
    isPathNotEmpty: Boolean,
    onUndo: () -> Unit,
    drawTextColor: MutableState<Color>
) {
    IconButton(onClick = onUndo, enabled = isPathNotEmpty) {
        Icon(
            imageVector = Icons.Default.Undo,
            contentDescription = "Undo",
            tint = if (isPathNotEmpty) Color.White else PrimaryMainDark
        )
    }
    Spacer(modifier = modifier.width(8.dp))
    FloatingActionButton(
        modifier = modifier.size(48.dp),
        onClick = {
            showColorPicker.value = true
        },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        backgroundColor = drawTextColor.value
    ) {
        if (showColorPicker.value) {
            ColorPickerDialog(
                modifier = modifier,
                initialColor = drawTextColor.value,
                onDismiss = {
                    showColorPicker.value = !showColorPicker.value
                },
                onPositiveClick = {
                    drawTextColor.value = it
                    showColorPicker.value = false
                },
                onNegativeClick = {
                    showColorPicker.value = !showColorPicker.value
                }
            )
        }
    }
}

@Composable
private fun ShowDrawMenu(
    modifier: Modifier,
    showColorPicker: MutableState<Boolean>,
    onUndo: () -> Unit,
    drawBrush: MutableState<Float>,
    drawColor: MutableState<Color>,
    isPathNotEmpty: Boolean,
) {
    val brushDensityLevel = remember {
        (5..15 step 5).toList()
    }
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

    FloatingActionButton(
        modifier = modifier.size(48.dp),
        onClick = {
            showColorPicker.value = true
        },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        backgroundColor = drawColor.value
    ) {
        if (showColorPicker.value) {
            ColorPickerDialog(
                modifier = modifier,
                initialColor = drawColor.value,
                onDismiss = {
                    showColorPicker.value = !showColorPicker.value
                },
                onPositiveClick = {
                    drawColor.value = it
                    showColorPicker.value = false
                },
                onNegativeClick = {
                    showColorPicker.value = !showColorPicker.value
                }
            )
        }
    }

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
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onDone: () -> Unit,
) {

    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    val path = ArrayList<Uri>()

    val fishbun = FishBun.with(context.getActivity()!!)
        .setImageAdapter(CoilAdapter())
        .setMaxCount(1)
        .setSelectedImages(path)
        .setThemeColor(PrimaryMain.toArgb())
        .hasCameraInPickerPage(hasCamera = true)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            if (it.containsValue(false)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.permission_rejected),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                fishbun.startAlbumWithActivityResultCallback(launcher)
            }
        }
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.54f))
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = {
            if (permissions.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                fishbun.startAlbumWithActivityResultCallback(launcher)
            } else {
                permissionLauncher.launch(permissions)
            }
        }) {
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

        TextButton(
            onClick = onDone
        ) {
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
private fun ColorPickerDialog(
    modifier: Modifier,
    initialColor: Color,
    onDismiss: () -> Unit,
    onPositiveClick: (Color) -> Unit,
    onNegativeClick: () -> Unit
) {
    var color by remember {
        mutableStateOf(initialColor)
    }
    Dialog(onDismissRequest = onDismiss) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ClassicColorPicker(
                modifier = modifier.height(200.dp),
                showAlphaBar = false,
                color = color,
                onColorChanged = {
                    color = it.toColor()
                })
            Spacer(modifier = modifier.height(12.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onNegativeClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = { onPositiveClick(color) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(text = "OK")
                }
            }
        }
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

private fun Uri.toBitmap(context: Context): Bitmap {
    val bitmap: Bitmap? = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    }
    return bitmap!!
}