package id.worx.device.client.screen.main

import android.Manifest
import android.app.Activity
import android.app.StatusBarManager
import android.content.Context
import android.icu.text.ListFormatter.Width
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.CoilAdapter
import com.sangcomz.fishbun.util.getRealPathFromURI
import id.worx.device.client.R
import id.worx.device.client.model.BarcodeField
import id.worx.device.client.model.BarcodeFieldValue
import id.worx.device.client.model.ImageField
import id.worx.device.client.model.ImageValue
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.components.getActivity
import id.worx.device.client.theme.DarkBackgroundStatusBar
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.util.BarcodeAnalyzer
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.ScannerViewModel
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    indexForm: Int = 2, //change later
    viewModel: DetailFormViewModel,
    scannerViewModel: ScannerViewModel
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val barcodeValue = remember {
        mutableStateOf("")
    }

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionState.launchPermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    Column(modifier = Modifier.fillMaxSize()) {
        when {
            permissionState.status.isGranted -> {
                Log.i("Permission", "Camera permission granted")
                ScannerView(
                    context = context,
                    indexForm = indexForm,
                    viewModel = viewModel,
                    scannerViewModel = scannerViewModel,
                    lifecycleOwner = lifecycleOwner,
                    barcodeValue = barcodeValue
                ) {
//                    resultNavigator.navigateBack(result = barcodeValue.value)
                    scannerViewModel.setResult(barcodeValue.value)
                    Log.d("TAG", "ScannerScreen: ${scannerViewModel.value}")
                }
            }
            permissionState.status.shouldShowRationale -> {
                Log.e("Permission", "Camera permission is needed to access the camera")
                Text(text = "Camera permission is needed to access the camera")
            }
        }
    }
}

@Composable
fun ScannerView(
    context: Context,
    indexForm: Int,
    viewModel : DetailFormViewModel,
    scannerViewModel : ScannerViewModel,
    lifecycleOwner: LifecycleOwner,
    barcodeValue: MutableState<String>,
    onScanDone: () -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    var flashStatus by remember { mutableStateOf(false) }
    val flashIcon =
        if (flashStatus) R.drawable.ic_flash_off else R.drawable.ic_flash_on
    val backgroundColorFlash =
        if (flashStatus) Color.Black.copy(alpha = 0.54f) else MaterialTheme.colors.onBackground

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val cameraExecutor = Executors.newSingleThreadExecutor()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()

    var requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val barcodeAnalyzer = BarcodeAnalyzer { barcodes ->
        barcodes.forEach { barcode ->
            barcode.rawValue?.let { value ->
                barcodeValue.value = value
                onScanDone()
            }
        }
    }

    val launcherGallery =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK &&
                    it.data != null
                ) {
                    it.data!!.getParcelableArrayListExtra<Uri>(FishBun.INTENT_PATH)
                        ?.forEach { uri ->
                            val fPath = getRealPathFromURI(context, uri)
                            scannerViewModel.goToPreviewBarcode(fPath)
                        }
                }
            })

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.containsValue(false)) {
            Toast.makeText(
                context,
                context.getString(R.string.permission_rejected),
                Toast.LENGTH_LONG
            ).show()
        } else {
            FishBun.with(context.getActivity()!!)
                .setImageAdapter(CoilAdapter())
                .setMaxCount(1)
                .setThemeColor(PrimaryMain.toArgb())
                .startAlbumWithActivityResultCallback(launcherGallery)
        }
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(cameraExecutor, barcodeAnalyzer)
        }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {
            previewView.apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
            update = { previewView ->
                cameraProviderFuture.addListener({
                    preview.also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    try {
                        var camera: Camera?
                        cameraProvider.apply {
                            unbindAll()
                            camera = bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        }
                        camera?.cameraControl?.enableTorch(flashStatus)
                    } catch (e: Exception) {
                        Log.e("Scanner", "Camera Preview: ${e.localizedMessage}")
                    }

                }, ContextCompat.getMainExecutor(context))
            }
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.54f))
        ) {
            val (icFlash, icBarcodeArea, icUpload) = createRefs()
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { flashStatus = !flashStatus }
                    .background(backgroundColorFlash)
                    .constrainAs(icFlash) {
                        top.linkTo(parent.top, 40.dp)
                        start.linkTo(parent.start, 16.dp)
                    },
            ) {
                Icon(
                    painter = painterResource(id = flashIcon),
                    contentDescription = "Flash",
                    tint = Color.White
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .constrainAs(icBarcodeArea) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_barcode_area),
                    contentDescription = "Scanner Area",
                    tint = Color.White
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .constrainAs(icUpload) {
                        bottom.linkTo(parent.bottom, 50.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        launcherPermission.launch(requiredPermissions)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_collections_24),
                    contentDescription = "Upload Image",
                    tint = Color.White
                )
                Text(
                    text = stringResource(id = R.string.upload_image).uppercase(),
                    style = Typography.subtitle1.copy(fontFamily = fontRoboto, color = Color.White),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}