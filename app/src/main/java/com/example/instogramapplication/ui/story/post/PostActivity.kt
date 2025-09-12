package com.example.instogramapplication.ui.story.post


import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.ActivityPostBinding
import com.example.instogramapplication.utils.PostUtils

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    // inisialize kamera yang di pake pertama
    private lateinit var camera: Camera
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    // request
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            val isGrantedAll = permission.all { it.value }
            if (isGrantedAll) {
                onPermissionGranted()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Gambar dipilih dari galeri
            navigateToEdit(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher.launch(requiredPermission)

        startCamera()
        setupListener()

        // back from edit
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_countainer)
            if (currentFragment == null || currentFragment !is EditFragment) {
                // Kalau tidak ada fragment, atau fragment edit sudah hilang
                startCamera()
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            postImageOpenGalery.setOnClickListener { openGalery() }
            postBtnSwitch.setOnClickListener { switchCamera() }
            postBtnClickCamera.setOnClickListener { takePhoto() }
            postBtnBack.setOnClickListener { finish() }

        }
    }

    private fun openGalery() {
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetResolution(Size(1440, 2560))
                .build()
                .also {
                    it.surfaceProvider = binding.postPreviewCamera.surfaceProvider
                }

            this.imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1440, 2560))
                .build()

            try {
                cameraProvider?.let { provider ->
                    provider.unbindAll()

                    camera = provider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                    setupCameraToZoom() // untuk bisa di zoom
                } ?: run {
                    // cameraProvider ternyata null
                    Log.e(TAG, "Camera provider is null")
                }
            } catch (exc: Exception) {
                Toast.makeText(
                    this@PostActivity,
                    getString(R.string.error_open_camera),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA

        startCamera()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCameraToZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera.cameraControl.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(this, listener)

        binding.postPreviewCamera.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun takePhoto() {
        val imageCapture = this.imageCapture ?: return

        val photoFile = PostUtils.createCustomTempFile(this)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@PostActivity,
                        getString(R.string.success_take_picture),
                        Toast.LENGTH_SHORT
                    ).show()

                    val uri = output.savedUri
                    uri?.let {
                        navigateToEdit(uri)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@PostActivity,
                        getString(R.string.error_take_picture),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        )
    }

    private fun navigateToEdit(uri: Uri) {
        val fragment = EditFragment.newInstance(uri.toString())

        stopCamera()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_countainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun onPermissionGranted() {
        val latestImageUri = PostUtils.getLatestImageUri(this)

        if (latestImageUri != null) {
            binding.postImageOpenGalery.setImageURI(latestImageUri)
        } else {
            Toast.makeText(this, "Tidak ada gambar ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    companion object{
        private val TAG = PostActivity::class.java.simpleName
    }
}
