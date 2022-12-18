package id.worx.device.client.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.util.concurrent.TimeUnit

class BarcodeAnalyzer(
    private var typeImage: Int = 0,
    private var context: Context? = null,
    private var filePath : String? = null,
    private val onBarcodeDetected: (barcodes: List<Barcode>) -> Unit,
) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimeStamp = 0L

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        val currentTimeStamp = System.currentTimeMillis()
        if (currentTimeStamp - lastAnalyzedTimeStamp >= TimeUnit.SECONDS.toMillis(1)) {
            image.image?.let { imageToAnalyze ->
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
                val barcodeScanner = BarcodeScanning.getClient(options)
                val imageToProcess = if (typeImage == 0) {
                    InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)
                } else {
                    context?.let { InputImage.fromFilePath(it, Uri.fromFile(File(filePath!!))) }
                }

                if (imageToProcess != null) {
                    barcodeScanner.process(imageToProcess)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                onBarcodeDetected(barcodes)
                            } else {
                                Log.e("Barocde Analyzer", "analyze: No barcode scanned")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Barocde Analyzer", "Barocde Analyzer: Something went wrong $e")
                        }
                        .addOnCompleteListener {
                            image.close()
                        }
                }
            }
            lastAnalyzedTimeStamp = currentTimeStamp
        } else {
            image.close()
        }
    }
}