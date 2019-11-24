package com.hussainmukadam.nearbyrestaurants

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hussainmukadam.nearbyrestaurants.adapter.NearbyRestaurantsAdapter
import com.hussainmukadam.nearbyrestaurants.model.NearbyRestaurants
import com.hussainmukadam.nearbyrestaurants.network.Status
import com.hussainmukadam.nearbyrestaurants.util.REQ_PERM_CODE
import com.hussainmukadam.nearbyrestaurants.util.getViewModel
import com.hussainmukadam.nearbyrestaurants.util.setSnackbar
import com.hussainmukadam.nearbyrestaurants.viewmodel.NearbyRestaurantsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private val nearbyRestaurantViewModel by lazy { getViewModel<NearbyRestaurantsViewModel>() }
    private val nearbyRestaurantsList: ArrayList<NearbyRestaurants> = ArrayList()
    private lateinit var nearbyRestaurantAdapter: NearbyRestaurantsAdapter
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupLayoutManager()

        buildLocationCallbacks()
    }

    private fun buildLocationCallbacks() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupLayoutManager() {
        rv_nearby_listing.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        nearbyRestaurantAdapter = NearbyRestaurantsAdapter(nearbyRestaurantsList) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        }
        rv_nearby_listing.adapter = nearbyRestaurantAdapter
    }

    private fun makeNearbyListingCall(lat: Double, long: Double) {
        nearbyRestaurantViewModel.fetchNearbyListing(lat, long).observe(this, Observer {
            if (it.loading) {
                tv_searched_location.text = getString(R.string.str_loading)
                pb_loading.visibility = View.VISIBLE
            } else {
                tv_searched_location.text = String.format(
                    Locale.ENGLISH,
                    getString(R.string.sf_restaurants_near, it.data?.location?.title)
                )
                pb_loading.visibility = View.GONE
            }

            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null && !it.data!!.nearbyRestaurants.isNullOrEmpty()) {
                        if (nearbyRestaurantsList.isNotEmpty())
                            nearbyRestaurantsList.clear()
                        nearbyRestaurantsList.addAll(it.data!!.nearbyRestaurants)
                        nearbyRestaurantAdapter.notifyDataSetChanged()
                        setEmptyState(false)
                    } else {
                        setEmptyState(true)
                    }
                }
                Status.ERROR -> {
                    val errorMessage = it.errorMessage ?: getString(R.string.something_went_wrong)
                    setSnackbar(cl_main_root_view, errorMessage)
                    setEmptyState(true, errorMessage)
                }
                Status.NO_INTERNET -> {
                    setSnackbar(cl_main_root_view, getString(R.string.no_internet_connection))
                    setEmptyState(true, getString(R.string.no_internet_connection))
                }
                Status.LOADING -> {
                }
            }
        })
    }

    private fun setEmptyState(
        flag: Boolean,
        reasonForEmpty: String = getString(R.string.nothing_found)
    ) {
        if (flag) {
            rv_nearby_listing.visibility = View.GONE
            tv_nothing_found.visibility = View.VISIBLE
            tv_nothing_found.text = reasonForEmpty
        } else {
            rv_nearby_listing.visibility = View.VISIBLE
            tv_nothing_found.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        if (googleApiClient.isConnected)
            googleApiClient.disconnect()
        super.onStop()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(ACCESS_FINE_LOCATION),
                REQ_PERM_CODE
            )
        } else {
            getLocationData()
        }
    }

    override fun onConnected(p0: Bundle?) {
        checkPermission()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("ConnectionCallback", "Connection Suspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("ConnectionCallback", "Connection Failed")
    }


    @SuppressLint("MissingPermission")
    private fun getLocationData() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                Log.d("Location Callback", "Lat ${location.latitude} long ${location.longitude}")
                makeNearbyListingCall(location.latitude, location.longitude)
            } else setSnackbar(cl_main_root_view, getString(R.string.location_not_avail))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationData()
            } else {
                setEmptyState(true, getString(R.string.needs_location))
                Snackbar.make(
                    cl_main_root_view,
                    getString(R.string.needs_location),
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Allow") {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, REQ_PERM_CODE)
                    }.show()
            }
        }
    }
}
