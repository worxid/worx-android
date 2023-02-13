package id.worx.device.client

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.data.database.Session
import io.sentry.Sentry
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var session: Session

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var lm: LocationManager
    private var isGpsActive: Boolean = false

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()){
            startLocationUpdates(this)
        }
    }

    override fun onPause() {
        if (allPermissionsGranted()){
            stopLocationUpdates()
        }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (allPermissionsGranted()) {
            getLocation(this)
            actionPermissionGranted()
        } else {
            permReqLauncher.launch(REQUIRED_PERMISSIONS)
        }
        setContentView(R.layout.activity_main)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    17072020)
            }
        }
    }

    private fun actionPermissionGranted(){}

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                getLocation(this)
                actionPermissionGranted()
            } else {
                Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.permission_rejected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun parseLocation(location: Location){
        session.saveLatitude(String.format("%.4f", location.latitude))
        session.saveLongitude(String.format("%.4f", location.longitude))
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(context: Context) {
        lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGpsActive = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (allPermissionsGranted()) {
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            location?.let {
                try {
                    parseLocation(it)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Log.d(packageName, "Can't get location : $e")
                }
            }
        }
        getLocationUpdates(this)
    }

    private fun getLocationUpdates(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 30000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 150f // 170 m = 0.1 mile
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {


            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    try {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            parseLocation(location)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d(packageName, "Can't get location : $e")
                    }
                }
            }
        }
    }

    //start location updates
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(context: Context) {
        lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGpsActive = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!this::locationRequest.isInitialized) {
            getLocationUpdates(context)
        }

        if (isGpsActive) {
            if (allPermissionsGranted()) {
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(context)

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                ) /* Looper */
            } else {
                permReqLauncher.launch(REQUIRED_PERMISSIONS)
            }
        } else {

        }
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}