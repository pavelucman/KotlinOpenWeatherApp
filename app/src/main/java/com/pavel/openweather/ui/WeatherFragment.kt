package com.pavel.openweather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.openweather.R
import com.example.openweather.databinding.WeatherFragmentBinding
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.weather_fragment.*
import javax.inject.Inject

class WeatherFragment : Fragment() {

    lateinit var dataBinding: WeatherFragmentBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val PERMISSION_ID = 22

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val weatherViewModel: WeatherViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.weather_fragment, container,false)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = weatherViewModel

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

        search_view.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(city: String?): Boolean {
                search_view.clearFocus()
                if (context!!.isConnected) {
                    weatherViewModel.showDataByLocation(city)
                } else {
                }
                return true
            }

            override fun onQueryTextChange(city: String?): Boolean {
                return true
            }
        })
        weatherViewModel.loadError.observe(viewLifecycleOwner, onErrorLiveDataObserver)

        weatherViewModel.loading.observe(viewLifecycleOwner, loadingLiveDataObserver)
    }

    private val onErrorLiveDataObserver = Observer<Boolean> { it ->
        if (requireContext().isConnected) {
            no_city.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private val loadingLiveDataObserver = Observer<Boolean> { isLoading ->
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            if (context!!.isConnected) {
                weatherViewModel.showDataByGeoLocation(
                    lastLocation.latitude.toString(),
                    lastLocation.longitude.toString()
                )
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions(requireActivity())) {
            if (isLocationEnabled(requireActivity())) {
                fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        if (requireContext().isConnected) {
                            weatherViewModel.showDataByGeoLocation(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissionsWeather(requireActivity())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getLastLocation()
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissionsWeather(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        }

    override fun onResume() {
        super.onResume()
        getLastLocation()
    }
}