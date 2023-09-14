package id.worx.device.client.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.databinding.PhotoScreenBinding
import id.worx.device.client.navigate
import id.worx.device.client.viewmodel.CameraViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class CameraPhotoFragment : Fragment() {
    private var _binding: PhotoScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var outputDirectory: File
    private val viewModel by activityViewModels<CameraViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (android.os.Build.VERSION.SDK_INT > 32){
            REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        }

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.CameraPhoto)
            }
        }

        _binding = PhotoScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInsets()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permReqLauncher.launch(REQUIRED_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()

        binding.btnSnap.setOnClickListener { takePhoto() }
        binding.btnFlash.setOnClickListener { switchFlash() }
        binding.btnSwitchCamera.setOnClickListener { switchCamera() }
        binding.tvCancel.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
    }

    override fun onDestroyView() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.status_bar_color, null)
        _binding = null
        super.onDestroyView()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clTopBar) { view, insets ->
            val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams

            if (marginLayoutParams.topMargin != insets.systemWindowInsetTop) {
                marginLayoutParams.updateMargins(top = insets.systemWindowInsetTop)
                view.parent.requestLayout()
            }

            insets
        }

        val isAttached = binding.clTopBar.isAttachedToWindow

        if (isAttached) {
            binding.clTopBar.invalidateInsets()
        } else {
            binding.clTopBar.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    binding.clTopBar.removeOnAttachStateChangeListener(this)
                    binding.clTopBar.invalidateInsets()
                }

                override fun onViewDetachedFromWindow(p0: View) {

                }
            })
        }
    }

    private fun takePhoto() {
        binding.camera.takePictureSnapshot()
    }

    private inner class Listener : CameraListener() {
        override fun onCameraOpened(options: CameraOptions) {
        }

        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat(
                    FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg"
            )
            FileOutputStream(photoFile.path).use { stream -> stream.write(result.data) }

            val msg = this@CameraPhotoFragment.requireContext().getString(R.string.photo_taken)
            Toast.makeText(
                this@CameraPhotoFragment.requireContext(),
                msg,
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, msg)

            viewModel.goToPreviewPhoto(photoFile.absolutePath)
        }

        override fun onExposureCorrectionChanged(
            newValue: Float,
            bounds: FloatArray,
            fingers: Array<PointF>?
        ) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
        }
    }

    private fun startCamera() {
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        binding.camera.setLifecycleOwner(this)
        binding.camera.addCameraListener(Listener())
    }

    private fun switchCamera() {
        if (binding.camera.facing == Facing.FRONT)
            binding.camera.facing = Facing.BACK
        else
            binding.camera.facing = Facing.FRONT
    }

    private fun switchFlash() {
        if (requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        ) {
            if (binding.camera.flash == Flash.AUTO || binding.camera.flash == Flash.OFF) {
                binding.camera.flash = Flash.TORCH
                binding.btnFlash.setColorFilter(resources.getColor(android.R.color.holo_red_light, null))
            } else {
                binding.camera.flash = Flash.OFF
                binding.btnFlash.setColorFilter(Color.WHITE)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.permission_rejected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    private fun View.invalidateInsets() {
        try {
            requestApplyInsets()
        } catch (e: Throwable) {

        }
    }

    companion object {
        private const val TAG = "WORX CameraFragment"
        private var REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}