package com.example.instogramapplication.ui.story.post

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import android.view.View
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

    // request
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    // pengacekan
    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri ->
        if (uri != null){
            // Gambar dipilih dari galeri
            Log.d(TAG, "Photo Picker selected")
            navigateToEdit(uri)
        }else{
            Log.d(TAG, "Photo Picker No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        startCamera()
        setupListener()

        // back from edit
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_countainer)
            if (currentFragment == null || currentFragment !is EditFragment){
                // Kalau tidak ada fragment, atau fragment edit sudah hilang
                Log.d(TAG, "Back to main, restart camera")
                startCamera()
            }
        }
    }

    private fun initView(){

    }

    private fun setupListener(){
        binding.apply {
            postImageOpenGalery.setOnClickListener { openGalery() }
            postBtnSwitch.setOnClickListener { switchCamera() }
            postBtnClickCamera.setOnClickListener { takePhoto() }

        }
    }

    private fun openGalery(){
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun startCamera(){
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
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )!!

                setupCameraToZoom() // untuk bisa di zoom
            }catch (exc: Exception) {
                Toast.makeText(
                    this@PostActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera(){
        cameraProvider?.unbindAll()
    }

    private fun switchCamera(){
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA

        startCamera()
    }

    private fun setupCameraToZoom(){
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
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

    private fun takePhoto(){
        val imageCapture = this.imageCapture ?: return

        val photoFile = PostUtils.createCustomTempFile(this)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@PostActivity,
                        "Berhasil mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()

                    val uri = output.savedUri
                    uri?.let {
                        navigateToEdit(uri)
                    }
                    Log.d(TAG, "onImageSaved: result $output")
                }
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@PostActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun navigateToEdit(uri: Uri){
        Log.d(TAG, "navigateToEdit: ")
        val fragment = EditFragment.newInstance(uri.toString())
        Log.d(TAG, "navigateToEdit: fagment $fragment")

        stopCamera()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_countainer, fragment)
            .addToBackStack(null)
            .commit()
    }


    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private val TAG = PostActivity::class.java.simpleName
    }
}