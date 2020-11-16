package pl.foxcode.crashalert.view

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pl.foxcode.crashalert.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val FINE_LOCATION_REQUEST_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        requestFuseLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }





    private fun requestFuseLocationPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(permission) -> {
                MaterialAlertDialogBuilder(applicationContext)
                    .setTitle(getString(R.string.permission_dialog_title))
                    .setMessage(getString(R.string.permission_dialog_message))
                    .setNeutralButton(R.string.ok, object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, i: Int) {
                            dialog?.dismiss()
                        }
                    })
                    .show()
            }
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_REQUEST_PERMISSION)
            }
        }
    }

    /*private fun onRequestPermissionsResult(requestCode : Int, permission: Array<out String>, grantResult: IntArray)
    {
        fun innerChceck(name:String){
            if(grantResult.isEmpty() || grantResult[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"permission refused", Toast.LENGTH_LONG).show()
            }
        }
    }*/

}