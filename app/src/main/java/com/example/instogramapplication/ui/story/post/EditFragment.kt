package com.example.instogramapplication.ui.story.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.instogramapplication.R
import com.example.instogramapplication.databinding.FragmentEditBinding
import com.example.instogramapplication.ui.main.MainActivity
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.DialogUtils.showToast
import com.example.instogramapplication.utils.ExtensionUtils.keyboardVisibilityFlow
import com.example.instogramapplication.utils.ExtensionUtils.reduceFileImage
import com.example.instogramapplication.utils.ExtensionUtils.setGradientText
import com.example.instogramapplication.utils.PostUtils
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.utils.constants.DialogType
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    private var currentLat: String? = null
    private var currentLon: String? = null

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireActivity())
    }

    private val viewModel: EditViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentImageUri = arguments?.getString(ARG_URI)?.toUri()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupListener()
        observer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        // terima uri gambar dari post act
        currentImageUri?.let { uri ->
            binding.imgvShowFromGalery.setImageURI(uri)
        }

        binding.postTvBerjalan.apply {
            setGradientText(
                ContextCompat.getColor(requireActivity(), R.color.color_variant),
                ContextCompat.getColor(requireActivity(), R.color.color_base)
            )
            isSelected = true
        }

        // handle back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmBack()
                }
            })

        // cek keyboard
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.root.keyboardVisibilityFlow().collect { isVisible ->
                    binding.let {
                        binding.dimOverlayCamera.isVisible = isVisible
                    }
                }
            }
        }
    }

    private fun setupListener() {
        binding.apply {
            postBtnBackToTake.setOnClickListener { confirmBack() }
            postBtnPosting.setOnClickListener { uploadStory() }
            postBtnAccessLocation.apply {
                setOnClickListener {
                    viewModel.toggleLocationSelected()
                }
            }
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.uploadState.collect { result ->
                when (result) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Error -> {
                        showError()
                        showLoading(false)
                    }

                    is Resource.Success -> {
                        showSuccess()
                        showLoading(false)
                    }

                    is Resource.Empty -> {
                        showLoading(false)
                    }

                    is Resource.ErrorConnection -> {
                        showLoading(false)
                        showToast(
                            requireContext().getString(R.string.error_koneksi),
                            requireActivity()
                        )
                    }
                }
            }
        }
        viewModel.isLocationSelected.observe(viewLifecycleOwner) {
            binding.postBtnAccessLocation.apply {
                isSelected = it
                Log.d(TAG, "observer: location is selected $isSelected")
                if (isSelected) {
                    checkLocationPermission()
                    setBackgroundResource(R.drawable.bg_round_button)
                } else
                    setBackgroundResource(0)
            }

        }
    }

    private fun showError() {
        DialogUtils.stateDialog(
            requireContext(),
            DialogType.ERROR,
            requireActivity().getString(R.string.popup_error_title),
            requireActivity().getString(R.string.popup_error_desc),
            requireActivity().getString(R.string.popup_error_btn)
        ) {
            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE

        }
    }

    private fun showSuccess() {
        DialogUtils.stateDialog(
            requireContext(),
            DialogType.SUCCESS,
            requireActivity().getString(R.string.popup_success_title),
            requireActivity().getString(R.string.popup_success_desc),
            requireActivity().getString(R.string.popup_success_btn),
        ) {
            showToast(requireActivity().getString(R.string.toast_success_upload), requireActivity())
            uploadSuccess()
            it.dismiss()
            binding.dimOverlay.visibility = View.INVISIBLE
        }
    }

    private fun confirmBack() {
        DialogUtils.confirmDialog(
            requireContext(),
            requireContext().getString(R.string.dialog_exit_edit_title),
            requireContext().getString(R.string.dialog_exit_edit_message),
            requireContext().getString(R.string.dialog_exit_edit_negative),
            requireContext().getString(R.string.dialog_exit_edit_positive)
        ) {
            backToTakePhoto()
        }
    }

    private fun backToTakePhoto() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = PostUtils.uriToFile(uri, requireActivity()).reduceFileImage()
            val desc = binding.postTvDesk.text.toString()

            if (desc.isNotBlank()) {
                val latToSend = if (binding.postBtnAccessLocation.isSelected) currentLat else null
                val lonToSend = if (binding.postBtnAccessLocation.isSelected) currentLon else null

                Log.d(TAG, "uploadStory: lat = $latToSend, lon = $lonToSend")
                viewModel.uploadStory(imageFile, desc, latToSend, lonToSend)
            } else {
                showToast(getString(R.string.error_empty_description), requireActivity())
            }
        } ?: showToast(getString(R.string.error_empty_image), requireActivity())

    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            dimOverlay.isVisible = isLoading
            loading.isVisible = isLoading
        }
    }

    private fun uploadSuccess() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @SuppressLint("MissingPermission") // karena kita sudah cek permission sebelumnya
    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLat = location.latitude.toString()
                    currentLon = location.longitude.toString()
                    Log.d("LOCATION", "Lat: $currentLat, Lon: ${currentLon}u")
                } else {
                    Toast.makeText(requireContext(), "Lokasi tidak tersedia", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                if (viewModel.isLocationSelected.value == true)
                    viewModel.toggleLocationSelected()
                Toast.makeText(
                    requireContext(),
                    "Gagal mendapatkan lokasi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if (viewModel.isLocationSelected.value == false)
                    viewModel.toggleLocationSelected()
                getCurrentLocation()
                Toast.makeText(requireContext(), "Kirim lokasi Anda saat ini", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.toggleLocationSelected()
                Log.d(TAG, "togel running")
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(
                        requireContext(),
                        "Izin lokasi diperlukan untuk fitur ini",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // user pilih "Don't ask again"
                    DialogUtils.confirmDialog(
                        requireContext(),
                        requireContext().getString(R.string.dialog_open_setting_edit_title),
                        requireContext().getString(R.string.dialog_open_setting_edit_message),
                        requireContext().getString(R.string.cancel),
                        requireContext().getString(R.string.yes),
                    ) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", requireContext().packageName, null)
                        }
                        startActivity(intent)
                    }
                }
            }
        }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    companion object {
        private const val ARG_URI = "image_uri"

        fun newInstance(imageUri: String): EditFragment {
            val fragment = EditFragment()
            val args = Bundle()
            args.putString(ARG_URI, imageUri)
            fragment.arguments = args
            return fragment
        }

        private val TAG = EditFragment::class.java.simpleName
    }


}