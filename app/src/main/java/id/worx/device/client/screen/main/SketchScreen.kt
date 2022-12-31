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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.screen.components.getActivity
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.PrimaryMainDark
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.util.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import kotlin.math.roundToInt

@Composable
fun SketchScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailFormViewModel,
    onBackNavigation: () -> Unit,
) {

    Scaffold(topBar = {
        WorxTopAppBar(onBack = onBackNavigation, title = "Draw Sketch", useProgressBar = false)
    }) { innerPadding ->
        Box(modifier = modifier
            .padding(innerPadding)
            .background(MaterialTheme.colors.secondary)
            .fillMaxSize()) {

            // TODO: Move variable that not used globally
            val context = LocalContext.current

            val state = viewModel.uiState.collectAsState()

            // Menu
            val currentMenu = remember {
                mutableStateOf(Menu.Default)
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

            val allowDrawing = remember {
                mutableStateOf(false)
            }

            // Image canvas
            var imageCanvasBitmap by remember {
                mutableStateOf<Bitmap?>(null)
            }

            // Text canvas
            val texts = remember {
                mutableStateListOf<Pair<String, TextProperties>>()
            }
            val tempText = remember {
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

            val cameraResultUri by viewModel.cameraResultUri.observeAsState()

            if (cameraResultUri != null) {
                imageCanvasBitmap = cameraResultUri!!.toBitmap(context)
            }

            Log.d("Camera Result URi", cameraResultUri.toString())
            Log.d("Image canvas", imageCanvasBitmap.toString())

            val launcherGallery =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
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
                allowDrawing = allowDrawing,
                isPathNotEmpty = paths.isNotEmpty(),
                showTextFieldOnCanvas = showTextFieldOnCanvas,
                onDone = {
                    currentMenu.value = Menu.Default
                    if (paths.isNotEmpty()) {
                        captureController.capture()
                    }
                    if (tempText.value.isNotEmpty()) {
                        texts.add(Pair(tempText.value,
                            TextProperties(color = drawTextColor.value)))
                        currentMenu.value = Menu.Default
                        tempText.value = ""
                        showTextFieldOnCanvas.value = false
                    }
                },
                onUndo = {
                    if (paths.isNotEmpty()) {
                        val lastItem = paths.last()
                        val lastPath = lastItem.first
                        val lastPathProperty = lastItem.second
                        paths.remove(lastItem)

                        pathsUndone.add(Pair(lastPath, lastPathProperty))
                    }
                })

            Capturable(modifier = modifier.fillMaxSize(),
                controller = captureController,
                onCaptured = { imageBitmap, error ->
                    if (imageBitmap != null) {
                        capturedBitmap.value = imageBitmap.asAndroidBitmap()
                    }
                    if (error != null) {
                        Log.e("WORX", "Sketch capture error: ${error.message}")
                    }
                }) {
                SketchCanvasView(modifier = modifier.fillMaxSize(),
                    paths = paths,
                    texts = texts,
                    pathsUndone = pathsUndone,
                    allowDrawing = allowDrawing,
                    drawBrush = drawBrush,
                    drawColor = drawColor,
                    drawTextColor = drawTextColor,
                    imageCanvas = imageCanvasBitmap,
                    tempText = tempText,
                    showTextFieldOnCanvas = showTextFieldOnCanvas,
                    currentMenu = currentMenu)
            }

            if (currentMenu.value == Menu.Default) {
                SketchBottomMenu(modifier = modifier.align(Alignment.BottomCenter),
                    context = context,
                    launcher = launcherGallery,
                    onDone = {
                        if (capturedBitmap.value != null) {
                            viewModel.saveSketch(capturedBitmap.value!!,
                                viewModel.uiState.value.currentComponent)
                            viewModel.cameraResultUri.value = null
                        } else {
                            captureController.capture()
                        }

                    },
                    navigateToPhotoCamera = {
                        viewModel.goToCameraPhoto(navigateFrom = MainScreen.Sketch)
                    })
            }

        }
    }
}

@Composable
private fun SketchCanvasView(
    modifier: Modifier,
    paths: SnapshotStateList<Pair<Path, PathProperties>>,
    pathsUndone: SnapshotStateList<Pair<Path, PathProperties>>,
    drawBrush: MutableState<Float>,
    drawColor: MutableState<Color>,
    imageCanvas: Bitmap?,
    showTextFieldOnCanvas: MutableState<Boolean>,
    tempText: MutableState<String>,
    drawTextColor: MutableState<Color>,
    texts: SnapshotStateList<Pair<String, TextProperties>>,
    currentMenu: MutableState<Menu>,
    allowDrawing: MutableState<Boolean>,
) {
    var currentPath by remember {
        mutableStateOf(Path())
    }

    var motionEvent by remember {
        mutableStateOf(MotionEvent.Idle)
    }

    var currentPosition by remember {
        mutableStateOf(Offset.Unspecified)
    }

    var previousPosition by remember {
        mutableStateOf(Offset.Unspecified)
    }

    // Draw canvas section
    val drawModifier = modifier
        .background(Color.White)
        .dragMotionEvent(
            onDragStart = {
                if (allowDrawing.value) {
                    motionEvent = MotionEvent.Down
                    currentPosition = it.position
                    if (it.pressed != it.previousPressed) it.consume()
                }
            },
            onDrag = {
                if (allowDrawing.value) {
                    motionEvent = MotionEvent.Move
                    currentPosition = it.position

                    if (it.positionChange() != Offset.Zero) it.consume()
                }
            },
            onDragEnd = {
                if (allowDrawing.value) {
                    motionEvent = MotionEvent.Up
                    if (it.pressed != it.previousPressed) it.consume()
                }
            })

    Canvas(modifier = drawModifier) {
        if (allowDrawing.value) {
            when (motionEvent) {
                MotionEvent.Down -> {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                    previousPosition = currentPosition
                }
                MotionEvent.Move -> {
                    currentPath.quadraticBezierTo(previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2)
                    previousPosition = currentPosition
                }
                MotionEvent.Up -> {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    paths.add(Pair(currentPath,
                        PathProperties(color = drawColor.value, stroke = drawBrush.value)))

                    currentPath = Path()
                    pathsUndone.clear()

                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }
                else -> Unit
            }
        }

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            imageCanvas?.let {
                drawImage(imageCanvas.asImageBitmap(),
                    topLeft = Offset(x = (size.width - it.width) / 2f,
                        y = (size.height - it.height) / 2f))
            }

            paths.forEach {
                val path = it.first
                val property = it.second

                drawPath(path = path, color = property.color, style = Stroke(property.stroke))
            }

            if (motionEvent != MotionEvent.Idle) {
                drawPath(path = currentPath,
                    color = drawColor.value,
                    style = Stroke(drawBrush.value))
            }
            restoreToCount(checkPoint)
        }
    }

    // Draw text section
    if (showTextFieldOnCanvas.value) {
        val focusRequester = remember {
            FocusRequester()
        }

        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.54f))
            .zIndex(2f)
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    showTextFieldOnCanvas.value = false
                    texts.add(Pair(tempText.value,
                        TextProperties(color = drawTextColor.value)))
                    currentMenu.value = Menu.Default
                    tempText.value = ""
                }
            }, contentAlignment = Alignment.Center) {
            TransparentTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = tempText.value,
                onValueChange = {
                    tempText.value = it
                },
                color = drawTextColor.value
            )
        }
    }

    Box(
        modifier = Modifier
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        texts.forEach {
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

            val text = it.first
            val textProperties = it.second

            Text(modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (!allowDrawing.value)
                                textProperties.offset = Offset(offsetX, offsetY)
                        },
                    ) { change, dragAmount ->
                        if (!allowDrawing.value) {
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                },
                text = text,
                color = textProperties.color,
                fontSize = 16.sp,
                fontFamily = fontRoboto
            )
        }

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
    showTextFieldOnCanvas: MutableState<Boolean>,
    drawTextColor: MutableState<Color>,
    currentMenu: MutableState<Menu>,
    allowDrawing: MutableState<Boolean>,
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .background(Color.Black.copy(0.54f))
        .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row {
            if (currentMenu.value != Menu.Default) {
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
                Menu.Default -> {
                    allowDrawing.value = false
                    showTextFieldOnCanvas.value = false
                    ShowDefaultMenu(onTextMenuClicked = {
                        currentMenu.value = Menu.Text
                    }, onDrawMenuClicked = {
                        currentMenu.value = Menu.Draw
                    })
                }
                Menu.Text -> {
                    allowDrawing.value = false
                    showTextFieldOnCanvas.value = true
                    ShowTextMenu(modifier = modifier,
                        showColorPicker = showColorPicker,
                        isPathNotEmpty = isPathNotEmpty,
                        drawTextColor = drawTextColor,
                        onUndo = onUndo)
                }
                Menu.Draw -> {
                    allowDrawing.value = true
                    showTextFieldOnCanvas.value = false
                    ShowDrawMenu(modifier = modifier,
                        showColorPicker = showColorPicker,
                        drawBrush = drawBrush,
                        drawColor = drawColor,
                        isPathNotEmpty = isPathNotEmpty,
                        onUndo = onUndo)
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
    drawTextColor: MutableState<Color>,
) {
    IconButton(onClick = onUndo, enabled = isPathNotEmpty) {
        Icon(imageVector = Icons.Default.Undo,
            contentDescription = "Undo",
            tint = if (isPathNotEmpty) Color.White else PrimaryMainDark)
    }
    Spacer(modifier = modifier.width(8.dp))
    FloatingActionButton(modifier = modifier.size(48.dp),
        onClick = {
            showColorPicker.value = true
        },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        backgroundColor = drawTextColor.value) {
        if (showColorPicker.value) {
            ColorPickerDialog(modifier = modifier, initialColor = drawTextColor.value, onDismiss = {
                showColorPicker.value = !showColorPicker.value
            }, onPositiveClick = {
                drawTextColor.value = it
                showColorPicker.value = false
            }, onNegativeClick = {
                showColorPicker.value = !showColorPicker.value
            })
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
        IconButton(modifier = modifier
            .clip(CircleShape)
            .background(color = if (selected) Color.Black.copy(0.54f) else Color.Transparent),
            onClick = {
                drawBrush.value = level.toFloat()
            }) {
            Icon(painter = when (level) {
                5 -> painterResource(id = R.drawable.ic_line_thin)
                10 -> painterResource(id = R.drawable.ic_line_medium)
                15 -> painterResource(id = R.drawable.ic_line_thick)
                else -> painterResource(id = -1)
            }, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = modifier.width(8.dp))
    }

    FloatingActionButton(modifier = modifier.size(48.dp),
        onClick = {
            showColorPicker.value = true
        },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        backgroundColor = drawColor.value) {
        if (showColorPicker.value) {
            ColorPickerDialog(modifier = modifier, initialColor = drawColor.value, onDismiss = {
                showColorPicker.value = !showColorPicker.value
            }, onPositiveClick = {
                drawColor.value = it
                showColorPicker.value = false
            }, onNegativeClick = {
                showColorPicker.value = !showColorPicker.value
            })
        }
    }

}

@Composable
private fun ShowDefaultMenu(onTextMenuClicked: () -> Unit, onDrawMenuClicked: () -> Unit) {
    IconButton(onClick = onTextMenuClicked, modifier = Modifier) {
        Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = Color.White)
    }

    IconButton(onClick = onDrawMenuClicked) {
        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, tint = Color.White)
    }
}

@Composable
private fun SketchBottomMenu(
    modifier: Modifier,
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    navigateToPhotoCamera: () -> Unit,
    onDone: () -> Unit,
) {

    val galleryPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    var cameraPermissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (Build.VERSION.SDK_INT > 32) {
        cameraPermissions = arrayOf(Manifest.permission.CAMERA)
    }

    val path = ArrayList<Uri>()

    val fishbun =
        FishBun.with(context.getActivity()!!).setImageAdapter(CoilAdapter()).setMaxCount(1)
            .setSelectedImages(path).setThemeColor(PrimaryMain.toArgb())
            .hasCameraInPickerPage(hasCamera = true)

    val galleryPermissionsLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                if (it.containsValue(false)) {
                    Toast.makeText(context,
                        context.getString(R.string.permission_rejected),
                        Toast.LENGTH_LONG).show()
                } else {
                    fishbun.startAlbumWithActivityResultCallback(launcher)
                }
            })

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                if (it.containsValue(false)) {
                    Toast.makeText(context,
                        context.getString(R.string.permission_rejected),
                        Toast.LENGTH_LONG).show()
                } else {
                    navigateToPhotoCamera()
                }
            })

    Row(modifier = modifier
        .fillMaxWidth()
        .background(Color.Black.copy(0.54f))
        .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {

        Row {
            IconButton(
                onClick = {
                    if (cameraPermissions.all {
                            ContextCompat.checkSelfPermission(context,
                                it) == PackageManager.PERMISSION_GRANTED
                        }) {
                        navigateToPhotoCamera()
                    } else {
                        cameraPermissionLauncher.launch(cameraPermissions)
                    }
                },
                modifier = Modifier.padding(end = 16.dp)) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera",
                    tint = Color.White,
                )
            }

            IconButton(onClick = {
                if (galleryPermissions.all {
                        ContextCompat.checkSelfPermission(context,
                            it) == PackageManager.PERMISSION_GRANTED
                    }) {
                    fishbun.startAlbumWithActivityResultCallback(launcher)
                } else {
                    galleryPermissionsLauncher.launch(galleryPermissions)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Gallery",
                    tint = Color.White,
                )
            }
        }

        TextButton(onClick = onDone) {
            Icon(imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.padding(end = 6.dp))
            Text(text = "DONE", fontSize = 16.sp, color = Color.White, fontFamily = fontRoboto)
        }
    }
}

@Composable
private fun ColorPickerDialog(
    modifier: Modifier,
    initialColor: Color,
    onDismiss: () -> Unit,
    onPositiveClick: (Color) -> Unit,
    onNegativeClick: () -> Unit,
) {
    var color by remember {
        mutableStateOf(initialColor)
    }
    Dialog(onDismissRequest = onDismiss) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ClassicColorPicker(modifier = modifier.height(200.dp),
                showAlphaBar = false,
                color = color,
                onColorChanged = {
                    color = it.toColor()
                })
            Spacer(modifier = modifier.height(12.dp))
            Row(modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = onNegativeClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                    Text(text = "Cancel")
                }
                TextButton(onClick = { onPositiveClick(color) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Composable
private fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    modifier: Modifier,
) {
    TextField(value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = TextStyle.Default.copy(fontFamily = fontRoboto, fontSize = 16.sp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = color,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ))
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