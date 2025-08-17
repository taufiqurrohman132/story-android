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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.instogramapplication.R
import com.example.instogramapplication.data.remote.model.StoryItem
import com.example.instogramapplication.databinding.FragmentMapsBinding
import com.example.instogramapplication.utils.ApiUtils
import com.example.instogramapplication.utils.DialogUtils
import com.example.instogramapplication.utils.PostUtils
import com.example.instogramapplication.utils.Resource
import com.example.instogramapplication.viewmodel.UserViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private val boundsBuilder = LatLngBounds.Builder()

    private val factory: UserViewModelFactory by lazy {
        UserViewModelFactory.getInstance(requireContext())
    }

    private val viewModel: MapsViewModel by viewModels {
        factory
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.apply {
//            addMarker(
//                MarkerOptions()
//                    .position(dicodingSpace)
//                    .title("Dicoding Space")
//                    .snippet("Batik Kumeli No.50")
//            )
            animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

            uiSettings.apply {
                isZoomControlsEnabled = true
                isIndoorLevelPickerEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = true
            }

//            setOnMapLongClickListener { latLng ->
//                addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title("New Marker")
//                        .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
//                )
//            }
        }

        binding.mapsOptions.setOnClickListener {
            val popUp = PopupMenu(requireContext(), it)
            popUp.menuInflater.inflate(R.menu.map_options, popUp.menu)
            popUp.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.normal_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                        true
                    }

                    R.id.satellite_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        true
                    }

                    R.id.terrain_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        true
                    }

                    R.id.hybrid_type -> {
                        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                        true
                    }

                    else -> {
                        super.onOptionsItemSelected(item)
                    }
                }
            }
            popUp.show()
        }

        getMyLocation()
        setMapStyle()
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
        mapFragment?.getMapAsync(callback)



        observer()
        setupListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupListener() {
        binding.addMarkerPeople.setOnClickListener { viewModel.loadStories(1) }
    }

    private fun getMyLocation() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addManyMarker() {

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventFlow.collect { message ->
                DialogUtils.showToast(message, requireActivity())
            }

        }
        viewModel.storiesState.observe(viewLifecycleOwner) { story ->
            Log.d(TAG, "observer: result = $story")
            when (story) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> showManyMarker(story.data)
//                    is Resource.Error -> showError()
//                    is Resource.ErrorConnection -> showErrorConnect(result.message)
                else -> "showEmpty()"
            }
        }
    }

    private fun showManyMarker(data: List<StoryItem>?) {
        data?.forEach { marker ->
            Log.d(TAG, "showManyMarker: letlang = $marker")
            if (marker.lat != null && marker.lon != null) {
                val latLng = LatLng(marker.lat, marker.lon)
                val imgName = ApiUtils.avatarUrl(requireContext(), marker.name ?: "AN")
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
                            mMap.addMarker(
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
                boundsBuilder.include(latLng)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                requireContext().resources.displayMetrics.widthPixels,
                requireContext().resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun showLoading(isLoading: Boolean) {


    }


    companion object {
        private val TAG = MapsFragment::class.java.simpleName
    }

}