package com.lucasmontano.openweathermap.map.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.lucasmontano.openweathermap.R
import com.lucasmontano.openweathermap.map.viewmodel.MapViewModel
import com.lucasmontano.openweathermap.model.domain.LocationWeatherModel
import kotlinx.android.synthetic.main.bottom_sheet_city.*
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener {

    private var isExploring: Boolean = true
    private lateinit var sheetBehavior: BottomSheetBehavior<MaterialCardView>
    private val mapViewModel: MapViewModel by viewModel()
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetBehavior = BottomSheetBehavior.from(cityBottomSheetLayout)
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bookmarkToggleButton.show()
                } else {
                    bookmarkToggleButton.hide()
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

        mapViewModel.pinWeatherLiveData.observe(this, Observer { locationWeatherModel ->
            addMarker(locationWeatherModel)
            mMap.setOnInfoWindowClickListener {
                expandMarker(locationWeatherModel)
            }
        })

        mapViewModel.bookmarksWeatherLiveData.observe(this, Observer {
            it.forEach { locationWeather ->
                addMarker(locationWeather)
            }
        })

        bookmarkToggleButton.setOnClickListener {
            mMap.clear()
            isExploring = if (isExploring) {
                mapViewModel.loadBookmarks()
                bookmarkToggleButton.text = getString(R.string.hide_bookmark)
                bookmarkToggleButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_adjust_pin)
                false
            } else {
                bookmarkToggleButton.text = getString(R.string.show_bookmark)
                bookmarkToggleButton.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark)
                true
            }
        }
    }

    private fun addMarker(locationWeatherModel: LocationWeatherModel) {
        val markerOptions = MarkerOptions()
            .position(LatLng(locationWeatherModel.lat, locationWeatherModel.lon))
            .title(locationWeatherModel.name)
        mMap.addMarker(markerOptions).apply {
            snippet = "${locationWeatherModel.weatherDescription} ( ${locationWeatherModel.temp} )"
            showInfoWindow()
        }
    }

    private fun expandMarker(locationWeather: LocationWeatherModel) {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        name.text = locationWeather.name.capitalize()
        maxMin.text = getString(
            R.string.max_min,
            locationWeather.tempMax.toString(),
            locationWeather.tempMin.toString()
        )
        description.text = locationWeather.weatherDescription?.capitalize()
        temp.text = getString(R.string.temp, locationWeather.temp.toString())
        humidity.text = getString(R.string.humidity, locationWeather.humidity.toString())
        overview.text = getString(
            R.string.overview,
            locationWeather.temp.toString(),
            locationWeather.feelsLike.toString(),
            locationWeather.windSpeed.toString(),
            locationWeather.pressure.toString()
        )
        sun.visibility = View.VISIBLE

        bookmark.setOnClickListener {
            mapViewModel.bookmarkLocation(locationWeather)
        }

        if (locationWeather.clouds > 10) {

            val animCloud: Animation = ScaleAnimation(
                0f, 1f * locationWeather.clouds / 100,
                1f, 1f,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f
            )
            animCloud.fillAfter = true
            animCloud.duration = 1000
            cloud.startAnimation(animCloud)

            val animShadown: Animation = ScaleAnimation(
                0f, 1f * locationWeather.clouds / 100,
                1f, 1f,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f
            )
            animShadown.fillAfter = true
            animShadown.duration = 900
            cloudShadow.startAnimation(animShadown)

            if (locationWeather.weatherDescription?.contains("rain") == true) {
                val animRain: Animation = ScaleAnimation(
                    0f, 1f * locationWeather.clouds / 100,
                    1f, 1f,
                    Animation.RELATIVE_TO_PARENT, 0.5f,
                    Animation.RELATIVE_TO_PARENT, 0.5f
                )
                animRain.fillAfter = true
                animRain.duration = 1000
                rain.startAnimation(animRain)
                rain.visibility = View.VISIBLE
            } else {
                rain.visibility = View.GONE
            }

            cloud.visibility = View.VISIBLE
            cloudShadow.visibility = View.VISIBLE

        } else {
            cloud.visibility = View.GONE
            cloudShadow.visibility = View.GONE

            if (locationWeather.weatherDescription?.contains("rain") == false) {
                rain.visibility = View.GONE
            }
        }
    }

    override fun onCameraMove() {
        if (isExploring) {
            mMap.clear()
            pinImageView.visibility = View.VISIBLE
        } else {
            pinImageView.visibility = View.GONE
        }
    }

    override fun onCameraIdle() {
        if (!isExploring) return
        pinImageView.visibility = View.GONE
        mapViewModel.refreshPinForecast(
            mMap.cameraPosition.target.latitude,
            mMap.cameraPosition.target.longitude
        )
    }
}
