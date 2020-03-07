package com.lucasmontano.openweathermap.map.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.lucasmontano.openweathermap.IOnBackPressed
import com.lucasmontano.openweathermap.R
import com.lucasmontano.openweathermap.databinding.BottomSheetCityBinding
import com.lucasmontano.openweathermap.databinding.FragmentMapsBinding
import com.lucasmontano.openweathermap.map.viewmodel.MapViewModel
import com.lucasmontano.openweathermap.model.domain.LocationWeatherModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener, IOnBackPressed {

    private var isExploring: Boolean = true
    private lateinit var sheetBehavior: BottomSheetBehavior<MaterialCardView>
    private val mapViewModel: MapViewModel by viewModel()
    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetDataBinding: BottomSheetCityBinding
    private lateinit var mapFragmentDataBinding: FragmentMapsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapFragmentDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        return mapFragmentDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.getBinding<BottomSheetCityBinding>(
            mapFragmentDataBinding.root.findViewById(
                R.id.cityBottomSheetLayout
            )

        )?.let { bottomSheetDataBinding = it }

        sheetBehavior = BottomSheetBehavior.from(bottomSheetDataBinding.cityBottomSheetLayout)
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mapFragmentDataBinding.bookmarkToggleButton.show()
                } else {
                    mapFragmentDataBinding.bookmarkToggleButton.hide()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
        sheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        addMap()
    }

    private fun addMap() {
        val mapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mapContainerLayout, mapFragment)
        fragmentTransaction.commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnMapClickListener(this)
        mMap.setOnInfoWindowClickListener {
            if (it.tag is LocationWeatherModel) {
                expandMarker(it.tag as LocationWeatherModel)
            }
        }

        mapViewModel.pinWeatherLiveData.observe(this, Observer { locationWeatherModel ->
            addMarker(locationWeatherModel)
        })

        mapViewModel.bookmarksWeatherLiveData.observe(this, Observer {
            mMap.clear()
            it.forEach { locationWeather ->
                addMarker(locationWeather)
            }
        })

        mapFragmentDataBinding.bookmarkToggleButton.setOnClickListener {
            mMap.clear()
            isExploring = if (isExploring) {
                mapViewModel.loadBookmarks()
                mapFragmentDataBinding.bookmarkToggleButton.text = getString(R.string.hide_bookmark)
                mapFragmentDataBinding.bookmarkToggleButton.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_adjust_pin)
                false
            } else {
                mapFragmentDataBinding.bookmarkToggleButton.text = getString(R.string.show_bookmark)
                mapFragmentDataBinding.bookmarkToggleButton.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark)
                true
            }
        }
    }

    private fun addMarker(locationWeatherModel: LocationWeatherModel) {
        val markerOptions = MarkerOptions()
            .position(LatLng(locationWeatherModel.lat, locationWeatherModel.lon))
            .title(locationWeatherModel.name)
        mMap.addMarker(markerOptions).apply {
            tag = locationWeatherModel
            snippet = "${locationWeatherModel.weatherDescription} ( ${locationWeatherModel.temp} )"
            showInfoWindow()
        }
    }

    private fun expandMarker(locationWeather: LocationWeatherModel) {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDataBinding.locationWeatherVB = locationWeather

        bottomSheetDataBinding.sun.visibility = View.VISIBLE

        bottomSheetDataBinding.addBookmarkButton.setOnClickListener {
            mapViewModel.bookmarkLocation(locationWeather)
            bottomSheetDataBinding.addBookmarkButton.visibility = View.GONE
            bottomSheetDataBinding.removeBookmarkButton.visibility = View.VISIBLE
        }

        bottomSheetDataBinding.removeBookmarkButton.setOnClickListener {
            mapViewModel.removeBookMark(locationWeather)
            bottomSheetDataBinding.removeBookmarkButton.visibility = View.GONE
            bottomSheetDataBinding.addBookmarkButton.visibility = View.VISIBLE
        }

        if (!isExploring) {
            bottomSheetDataBinding.removeBookmarkButton.visibility = View.VISIBLE
            bottomSheetDataBinding.addBookmarkButton.visibility = View.GONE
        } else {
            bottomSheetDataBinding.addBookmarkButton.visibility = View.VISIBLE
            bottomSheetDataBinding.removeBookmarkButton.visibility = View.GONE
        }

        if (locationWeather.clouds >= 10) {
            showCloudAnim(locationWeather)
            if (locationWeather.weatherDescription?.contains("rain") == true) {
                showRainAnim(locationWeather)
            } else {
                hideRainAnim()
            }
        } else {
            hideCloudAnim()

            if (locationWeather.weatherDescription?.contains("rain") == false) {
                hideRainAnim()
            }
        }
    }

    private fun hideRainAnim() {
        val animRain: Animation = ScaleAnimation(
            1f, 0f,
            1f, 1f,
            Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animRain.fillAfter = true
        animRain.duration = 1000
        bottomSheetDataBinding.rain.startAnimation(animRain)
        bottomSheetDataBinding.rain.visibility = View.GONE
    }

    private fun hideCloudAnim() {
        val animCloud: Animation = ScaleAnimation(
            1f, 0f,
            1f, 1f,
            Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animCloud.fillAfter = true
        animCloud.duration = 1000
        bottomSheetDataBinding.cloud.startAnimation(animCloud)

        val animShadown: Animation = ScaleAnimation(
            1f, 0f,
            1f, 1f,
            Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animShadown.fillAfter = true
        animShadown.duration = 900
        bottomSheetDataBinding.cloudShadow.startAnimation(animShadown)

        bottomSheetDataBinding.cloud.visibility = View.GONE
        bottomSheetDataBinding.cloudShadow.visibility = View.GONE
    }

    private fun showRainAnim(locationWeather: LocationWeatherModel) {
        val animRain: Animation = ScaleAnimation(
            0f, 1f * locationWeather.clouds / 100,
            1f, 1f,
            Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animRain.fillAfter = true
        animRain.duration = 1000
        bottomSheetDataBinding.rain.startAnimation(animRain)
        bottomSheetDataBinding.rain.visibility = View.VISIBLE
    }

    private fun showCloudAnim(locationWeather: LocationWeatherModel) {
        val animCloud: Animation = ScaleAnimation(
            0f, 1f * locationWeather.clouds / 100,
            1f, 1f,
            Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animCloud.fillAfter = true
        animCloud.duration = 1000
        bottomSheetDataBinding.cloud.startAnimation(animCloud)

        val animShadown: Animation = ScaleAnimation(
            0f, 1f * locationWeather.clouds / 100,
            1f, 1f,
            Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0.5f
        )
        animShadown.fillAfter = true
        animShadown.duration = 900
        bottomSheetDataBinding.cloudShadow.startAnimation(animShadown)

        bottomSheetDataBinding.cloud.visibility = View.VISIBLE
        bottomSheetDataBinding.cloudShadow.visibility = View.VISIBLE
    }

    override fun onCameraMove() {
        if (isExploring) {
            mMap.clear()
            mapFragmentDataBinding.pinImageView.visibility = View.VISIBLE
        } else {
            mapFragmentDataBinding.pinImageView.visibility = View.GONE
        }
    }

    override fun onCameraIdle() {
        if (!isExploring) return
        mapFragmentDataBinding.pinImageView.visibility = View.GONE
        mapViewModel.refreshPinForecast(
            mMap.cameraPosition.target.latitude,
            mMap.cameraPosition.target.longitude
        )
    }

    override fun onBackPressed(): Boolean {
        return if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            true
        } else {
            false
        }
    }

    override fun onMapClick(latLng: LatLng?) {
        if (isExploring) {
            mMap.clear()
            mapFragmentDataBinding.pinImageView.visibility = View.VISIBLE
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        } else {
            mapFragmentDataBinding.pinImageView.visibility = View.GONE
        }
    }
}
