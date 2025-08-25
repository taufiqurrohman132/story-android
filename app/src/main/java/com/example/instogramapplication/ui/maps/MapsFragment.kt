package com.example.instogramapplication.ui.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.instogramapplication.R
import com.example.instogramapplication.data.local.entity.StoryEntity
import com.example.instogramapplication.databinding.FragmentMapsBinding
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.PostUtils
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var mMap: GoogleMap? = null

    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireContext())
    }

    private val viewModel: MapsViewModel by viewModels {
        factory
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        observer()
        setupListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap

        mMap?.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isIndoorLevelPickerEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = true
                isMyLocationButtonEnabled = false
            }
        }

        setMapStyle()
    }

    private fun setupListener() {
        binding.apply {
            mapsFloatingMenuMode.setOnMenuToggleListener { opened ->
                if (opened) {
                    mapsFloatingMenuLocation.close(true)
                }
            }
            mapsFloatingMenuLocation.setOnMenuToggleListener { opened ->
                if (opened) {
                    mapsFloatingMenuMode.close(true)
                }
            }

            normalType.setOnClickListener {
                mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                closeMenus()
            }
            terrainType.setOnClickListener {
                mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                closeMenus()
            }
            hybridType.setOnClickListener {
                mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
                closeMenus()
            }
            satelliteType.setOnClickListener {
                mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                closeMenus()
            }

            fabUserStory.setOnClickListener {
                // action user story di sini
                viewModel.loadstoriesForMap()
                closeMenus()
            }
            fabMyLocation.setOnClickListener {
                getMyLocation()
                closeMenus()
            }
        }
    }

    private fun closeMenus() {
        binding.mapsFloatingMenuMode.close(true)
        binding.mapsFloatingMenuLocation.close(true)
    }


    private fun getMyLocation() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true

            // ambil lokasi terahir
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                } else {
                    Toast.makeText(requireContext(), "Lokasi tidak ditemukan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap?.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )
            if (success != true) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun observer() {
//        viewModel.pagingStories.observe(viewLifecycleOwner) { data ->
//
//        }
        viewModel.storiesForMap.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()){
                viewModel.loadstoriesForMap()
            }else
                showManyMarker(list) // kalau perlu convert ke StoryItem
            Log.d(TAG, "observer: list lokasi $list")
        }
    }



    private fun showManyMarker(data: List<StoryEntity>?) {
        val indonesiaPoints = mutableListOf<LatLng>()
        val globalPoints = mutableListOf<LatLng>()

        data?.forEach { marker ->
            var safeLat = marker.lat ?: return@forEach
            var safeLon = marker.lon ?: return@forEach

            // normalisasi microdegrees
            if (safeLat !in -90.0..90.0 || safeLon !in -180.0..180.0) {
                if (kotlin.math.abs(safeLat) > 1000 || kotlin.math.abs(safeLon) > 1000) {
                    safeLat /= 1_000_000.0
                    safeLon /= 1_000_000.0
                }
            }
            if (safeLat !in -90.0..90.0 || safeLon !in -180.0..180.0) return@forEach

            val latLng = LatLng(safeLat, safeLon)
            globalPoints.add(latLng)

            if (safeLat in -11.0..6.0 && safeLon in 95.0..141.0) {
                indonesiaPoints.add(latLng)
            }

            val imgName = ApiUtils.avatarUrl(requireContext(), marker.name ?: "JK")
            Glide.with(requireContext())
                .asBitmap()
                .load(imgName)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val markerIcon =
                            PostUtils.createBalloonMarker(requireContext(), resource)
                        mMap?.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(marker.name)
                                .snippet(marker.description)
                                .icon(markerIcon)
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }

        // pilih target points: prioritaskan Indonesia
        val targetPoints = indonesiaPoints.ifEmpty { globalPoints }

        // kalau kosong, jangan build bounds
        if (targetPoints.isEmpty()) {
            Log.w(TAG, "Belum ada lokasi valid, skip animateCamera")
            return
        }

        if (targetPoints.size == 1) {
            // cuma 1 titik , zoom ke titik tsb
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPoints[0], 12f))
        } else {
            // bikin builder baru setiap kali update
            val boundsBuilder = LatLngBounds.Builder()
            targetPoints.forEach { boundsBuilder.include(it) }

            try {
                val bounds = boundsBuilder.build()
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        requireContext().resources.displayMetrics.widthPixels,
                        requireContext().resources.displayMetrics.heightPixels,
                        300
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Gagal build bounds: ${e.message}")
            }
        }
    }

    companion object {
        private val TAG = MapsFragment::class.java.simpleName
    }

}