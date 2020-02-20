package com.lucasmontano.openweathermap.map.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lucasmontano.openweathermap.R
import com.lucasmontano.openweathermap.map.viewmodel.MapViewModel
import com.lucasmontano.openweathermap.model.domain.CityForecastModel
import kotlinx.android.synthetic.main.bottom_sheet_city.*
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener {

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
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

        mapViewModel.pinForecastLiveData.observe(this, Observer { cityForecast ->
            val markerOptions = MarkerOptions()
                .position(LatLng(cityForecast.lat, cityForecast.lon))
                .title(cityForecast.name)
            mMap.addMarker(markerOptions).apply {
                snippet = "${cityForecast.weatherDescription} ( ${cityForecast.temp} )"
                showInfoWindow()
            }
            mMap.setOnInfoWindowClickListener {
                expandMarker(cityForecast)
            }
        })
    }

    private fun expandMarker(cityForecast: CityForecastModel) {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        description.text = cityForecast.weatherDescription
    }

    override fun onCameraMove() {
        mMap.clear()
        pinImageView.visibility = View.VISIBLE
    }

    override fun onCameraIdle() {
        pinImageView.visibility = View.GONE
        mapViewModel.refreshPinForecast(
            mMap.cameraPosition.target.latitude,
            mMap.cameraPosition.target.longitude
        )
    }
}
