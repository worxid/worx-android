package id.worx.device.client.screen.main

import android.Manifest
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.SwipeButton
import id.worx.device.client.screen.components.SwipeDirection
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.util.getTimeString
import id.worx.device.client.viewmodel.HomeViewModelImpl
import id.worx.device.client.viewmodel.PunchStatus
import id.worx.device.client.viewmodel.PunchViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PunchScreen(
    modifier: Modifier = Modifier,
    punchViewModel: PunchViewModel,
    username: String,
    location: String,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by punchViewModel.state.collectAsState()

    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    var lensFacing by remember {
        mutableStateOf(CameraSelector.LENS_FACING_BACK)
    }
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val previewView = remember {
        PreviewView(context)
    }

//    LaunchedEffect(key1 = lensFacing, block = {
    if (cameraPermissionState.allPermissionsGranted) {
        LaunchedEffect(key1 = lensFacing, block = {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        })
    } else {
        LaunchedEffect(key1 = Unit, block = {
            cameraPermissionState.launchMultiplePermissionRequest()
        })
    }
//    })

    Scaffold(
        topBar = {
            WorxTopAppBar(onBack = {
                navigateBack()

            }, title = "Punch", useProgressBar = false)
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (statusBox, cameraControllers, cameraView, swipeButton) = createRefs()

            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .constrainAs(cameraView) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(swipeButton.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )

            Row(modifier = Modifier
                .padding(top = 24.dp, end = 16.dp)
                .background(MaterialTheme.colors.primary, CircleShape)
                .constrainAs(cameraControllers) {
                    top.linkTo(cameraView.top)
                    end.linkTo(cameraView.end)
                }) {
                IconButton(
                    onClick = { /*TODO*/ },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOff,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 222.dp)
                    .padding(16.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .constrainAs(statusBox) {
                        start.linkTo(cameraView.start)
                        bottom.linkTo(cameraView.bottom)
                    }
            ) {
                StatusBoxItem(icon = Icons.Default.Person, text = username)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBoxItem(icon = Icons.Default.Schedule, text = uiState.localTime ?: "01 Jan 1990 00:00:00")
                Spacer(modifier = Modifier.height(8.dp))
                StatusBoxItem(icon = Icons.Default.Timelapse, text = uiState.timer.getTimeString())
            }

            SwipeButton(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 16.dp)
                    .constrainAs(swipeButton) {
                        start.linkTo(cameraView.start)
                        end.linkTo(cameraView.end)
                        bottom.linkTo(parent.bottom)
                    },
                icon = {
                    Icon(
                        modifier = Modifier.padding(16.dp),
                        imageVector = if (uiState.punchStatus == PunchStatus.OUT) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                text = if (uiState.punchStatus == PunchStatus.OUT) stringResource(id = R.string.swipe_to_clock_in) else stringResource(
                    id = R.string.swipe_to_clock_out
                ),
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 0.dp,
                shape = CircleShape,
                swipeDirection = if (uiState.punchStatus == PunchStatus.OUT) SwipeDirection.LeftToRight else SwipeDirection.RightToLeft
            ) {
                if (uiState.punchStatus == PunchStatus.OUT)
                    punchViewModel.clockIn()
                else
                    punchViewModel.clockOut()

            }
        }
    }
}

@Composable
private fun StatusBoxItem(modifier: Modifier = Modifier, icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = modifier.size(12.dp),
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = modifier.width(10.dp))
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PunchScreenPreview() {
    WorxTheme {
//        PunchScreen(punchViewModel = punchViewModel)
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}