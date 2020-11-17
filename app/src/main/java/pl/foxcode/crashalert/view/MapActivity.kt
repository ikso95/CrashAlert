package pl.foxcode.crashalert.view

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.navigation_header.*
import pl.foxcode.crashalert.R
import pl.foxcode.crashalert.model.DatabaseMarker
import pl.foxcode.crashalert.model.DatabaseUser
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentCenter: LatLng
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReference2: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mAuth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("Data")
        databaseReference2 = database.getReference("Users")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                lastLocation = locationResult.lastLocation
            }
        }

        createLocationRequest()


        //FAB onClick Actions
        floatingActionButton_setMarker.setOnClickListener {
            Log.d("marker", "onCreate: " + currentCenter)
            placeMarkerOnMap(currentCenter)
            imageView_marker.visibility = View.INVISIBLE
            floatingActionButton_setMarker.visibility = View.GONE
            floatingActionButton_sendMarker.visibility = View.VISIBLE
            floatingActionButton_cancel.visibility = View.VISIBLE
        }

        floatingActionButton_sendMarker.setOnClickListener {

            floatingActionButton_sendMarker.visibility = View.GONE
            floatingActionButton_cancel.visibility = View.GONE
            floatingActionButton_setMarker.visibility = View.VISIBLE
            map.clear()
            imageView_marker.visibility = View.VISIBLE

            val databaseMarkerInput = DatabaseMarker(
                currentCenter.longitude.toString(),
                currentCenter.latitude.toString(),
                mAuth.currentUser!!.uid,
                Date().time.toString()
            )
            databaseReference.child("${Date().time}").setValue(databaseMarkerInput)

            val databaseUserInput = DatabaseUser("regular", 0)
            databaseReference2.child(mAuth.currentUser!!.uid).setValue(databaseUserInput)   ///////////////////////////////////////

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.success_title))
                .setMessage(getString(R.string.success_data_sent))
                .setPositiveButton(
                    getString(R.string.ok),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                        }
                    })
                .setIcon(R.drawable.ic_check)
                .show()
        }

        floatingActionButton_cancel.setOnClickListener {
            floatingActionButton_sendMarker.visibility = View.GONE
            floatingActionButton_cancel.visibility = View.GONE
            floatingActionButton_setMarker.visibility = View.VISIBLE
            map.clear()
            imageView_marker.visibility = View.VISIBLE
        }

        floatingActionButton_open_menu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
            textView_user_email.text = mAuth.currentUser?.email
            textView_user_ID.text = mAuth.currentUser?.uid
        }


        //navigation drawer onClick actions
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.menu_item_change_email) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.dialog_change_email_title))
                    .setMessage(getString(R.string.dialog_chane_email_message))
                    .setPositiveButton(
                        getString(R.string.ok),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                                val user = mAuth.currentUser
                                if (user != null)
                                    user.updateEmail(mAuth.currentUser?.email.toString())
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.reset_email),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    .setNegativeButton(
                        getString(R.string.cancel),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                            }
                        })
                    .setIcon(R.drawable.ic_action_warning)
                    .show()
            }
            if (it.itemId == R.id.menu_item_change_password) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.dialog_change_password_title))
                    .setMessage(getString(R.string.dialog_chane_password_message))
                    .setPositiveButton(
                        getString(R.string.ok),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface: DialogInterface?, p1: Int) {
                                mAuth.sendPasswordResetEmail(mAuth.currentUser!!.email.toString())
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.reset_password),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    .setNegativeButton(
                        getString(R.string.cancel),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                            }
                        })
                    .setIcon(R.drawable.ic_action_warning)
                    .show()
            }
            if (it.itemId == R.id.menu_item_log_out) {
                mAuth.signOut()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
            }
            false
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestFuseLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }

        map.setOnCameraIdleListener(object : GoogleMap.OnCameraIdleListener {
            override fun onCameraIdle() {
                val cameraPosition = map.cameraPosition
                currentCenter = cameraPosition.target
            }
        })
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
            .draggable(true)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_crash_marker2
                )
            )
        )
        map.addMarker(markerOptions)
    }


    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun createLocationRequest() {

        locationRequest = LocationRequest()
        locationRequest.interval = 60000 // 1 minute
        locationRequest.fastestInterval = 60000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this@MapActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }


    private fun requestFuseLocationPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(permission) -> {
                MaterialAlertDialogBuilder(applicationContext)
                    .setTitle(getString(R.string.permission_dialog_title))
                    .setMessage(getString(R.string.permission_dialog_message))
                    .setNeutralButton(R.string.ok, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, i: Int) {
                            dialog?.dismiss()
                        }
                    })
                    .show()
            }
            else -> {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


}