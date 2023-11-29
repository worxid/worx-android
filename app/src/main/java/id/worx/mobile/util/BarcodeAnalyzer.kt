package id.worx.mobile.util

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.TimeUnit

class BarcodeAnalyzer(
    private val barcodeType: String,
    private val onBarcodeDetected: (barcodes: List<Barcode>) -> Unit,
) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimeStamp = 0L

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        val currentTimeStamp = System.currentTimeMillis()
        if (currentTimeStamp - lastAnalyzedTimeStamp >= TimeUnit.SECONDS.toMillis(1)) {
            image.image?.let { imageToAnalyze ->
                val options = BarcodeScannerOptions.Builder()
                Log.d("TAG", "analyze: $barcodeType")
                if (barcodeType == "all") {
                    options.setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                } else {
                    options.setBarcodeFormats(
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.TYPE_ISBN,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_ITF,
                        Barcode.FORMAT_CODE_93,
                        Barcode.FORMAT_CODABAR
                    )
                }
                val barcodeScanner = BarcodeScanning.getClient(options.build())
                val imageToProcess =
                    InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

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
            lastAnalyzedTimeStamp = currentTimeStamp
        } else {
            image.close()
        }
    }
}