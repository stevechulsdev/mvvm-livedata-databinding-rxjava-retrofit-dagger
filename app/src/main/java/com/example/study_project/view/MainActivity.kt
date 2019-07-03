package com.example.study_project.view

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.study_project.R
import com.example.study_project.common.Constants.Companion.REQUEST_PERMISSION_CODE
import com.example.study_project.common.Constants.Companion.REQUIRED_PERMISSIONS
import com.example.study_project.databinding.ActivityMainBinding
import com.example.study_project.di.ViewModelFactory
import com.example.study_project.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mainViewModel: MainViewModel

    private var mapFragment: SupportMapFragment? = null

    private var mMap: GoogleMap? = null

    private var myLat: Double = 0.0
    private var myLng: Double = 0.0

    private var markerOption: MarkerOptions = MarkerOptions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        binding.vm = mainViewModel

        // set google map
        initGoogleMap()
    }

    override fun onDestroy() {
        mainViewModel.onDestroy()
        super.onDestroy()
    }

    /**
     * init google map
     */
    private fun initGoogleMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.fm_map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * check permission
     */
    private fun checkPermission() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0])

        // 권한이 있는 경우
        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            mMap?.let {
                it.apply {
                    setLocation()

                    isMyLocationEnabled = true

                    setOnCameraIdleListener(this@MainActivity)
                }
            }
        }
        else { // 권한이 없는 경우, request permission
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
    }

    /**
     * set location service
     * get last location and zoom
     */
    private fun setLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.let { it ->
            it.lastLocation.addOnSuccessListener(this) {location: Location? ->
                location?.let {
                    // get last location or current location
                    val lastLatLng = LatLng(it.latitude, it.longitude)

                    // camera move to current location
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12f))
                }
            }

            // use current location button
            mMap?.let {
                it.apply {
                    isMyLocationEnabled = true
                    setOnCameraIdleListener(this@MainActivity)
                }
            }
        }
    }

    /**
     * add to marker into google map
     * @param markerLocation 마커 찍을 location as LatLng
     * @param pm10 미세먼지 수치
     * @param pm25 초미세먼지 수치
     */
    private fun addMarker(markerLocation: LatLng, pm10: String, pm25: String) {
        // set marker options
        markerOption.apply {
            position(markerLocation)
            title("미세먼지 : $pm10")
            snippet("초미세먼지 : $pm25")
        }

        mMap?.addMarker(markerOption)?.showInfoWindow()
    }

    /**
     * set LiveData Observe
     */
    private fun setObserver() {
        mainViewModel.dataResponse.observe(this, Observer {
            // get center position as LatLng
            val currentLocation = LatLng(myLat, myLng)

            // add marker and title, snippet
            addMarker(currentLocation, it[0], it[1])
        })
    }

    /**
     * ready google map
     * @param googleMap
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            mMap = it

            // set observer
            setObserver()

            // check permission
            checkPermission()
        }
    }

    /**
     * permission result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_PERMISSION_CODE -> {
                for (i in 0 until REQUIRED_PERMISSIONS.size) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // 권한이 없으면 종료
                        finish()
                    }
                }

                // set location service & move to current position & add to marker
                mMap?.let {
                    it.apply {
                        setLocation()

                        isMyLocationEnabled = true

                        setOnCameraIdleListener(this@MainActivity)
                    }
                }
            }
        }
    }

    /**
     * Google map finish movement event listener
     */
    override fun onCameraIdle() {
        // get currentLatitude, currentLongitude
        mMap?.let {
            myLat = it.cameraPosition.target.latitude
            myLng = it.cameraPosition.target.longitude
        }

        // api call
        mainViewModel.getAirData(myLat, myLng)
    }
}
