package com.iviplay.gozemtest.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iviplay.gozemtest.R
import com.iviplay.gozemtest.data.model.ContentData
import com.iviplay.gozemtest.data.model.UserRegisterModel
import com.iviplay.gozemtest.databinding.ActivityMainBinding
import com.iviplay.gozemtest.databinding.DialogChoixMenuBinding
import com.iviplay.gozemtest.repository.ContentRepository
import com.iviplay.gozemtest.repository.UserRepository
import com.iviplay.gozemtest.utils.SharedPreferenceManager
import com.iviplay.gozemtest.utils.Toolkit


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOGTAG = MainActivity::class.java.simpleName
    private lateinit var bindingMainActivity: ActivityMainBinding
    private var map: SupportMapFragment? = null
    private lateinit var toolkit: Toolkit
    private lateinit var user : UserRegisterModel
    private var getData :  ArrayList<ContentData>? = arrayListOf()
    private var mBottomSheetBehaviorMobility: BottomSheetBehavior<*>? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainActivity = ActivityMainBinding.inflate(layoutInflater)
        val view = bindingMainActivity.root
        setContentView(view)
        toolkit = Toolkit(applicationContext)
        user = SharedPreferenceManager.getInstance(this@MainActivity)!!.getUserResponse()

        //user card info
        bindingMainActivity.fullnameText.text = user.fullName
        bindingMainActivity.emailText.text = user.email

        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if(!gps_enabled && !network_enabled)
            toolkit.displayPromptForEnablingGPS(this)

        requestGetApi()
        initView()

        map = supportFragmentManager.findFragmentById(R.id.google_map_mobility) as? SupportMapFragment
        map!!.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        bindingMainActivity.headerLayout.iconAccount.setOnClickListener {
            showSupportBottomSheet(user.fullName.toString(), user.email.toString())
        }

        bindingMainActivity.headerLayout.menu.setOnClickListener {
            showChoixMenuDialog()
        }
    }


    private fun initView(){
        supportActionBar!!.hide()

        if(intent.hasExtra("email")){
            val fullname = intent.getStringExtra("fullname").toString()
            val email = intent.getStringExtra("email").toString()
            val pass = intent.getStringExtra("pass").toString()

            bindingMainActivity.emailText.text = email
            bindingMainActivity.fullnameText.text = fullname

        }
    }

    // call api pour recuperer les donnees (profil, map, information)
    private fun requestGetApi() : ArrayList<ContentData>{
       ContentRepository.getInstance().fetchData(user.token!!) { isSuccess, response ->
            if (!isSuccess) {
               Log.e(LOGTAG, "$response")
                getData = response
            } else {
                getData = null
                Toast.makeText(
                    applicationContext,
                    "Une erreur s'est produite, veuillez ressayer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(LOGTAG, "$response")
            }
        }
        return getData!!
    }



    private  fun showSupportBottomSheet(fullname: String, email: String) {
        mBottomSheetBehaviorMobility = BottomSheetBehavior.from(bindingMainActivity.bottomSheetUser.userLayoutBottomsheet)

        if (mBottomSheetBehaviorMobility!!.state == BottomSheetBehavior.STATE_HIDDEN ||
            mBottomSheetBehaviorMobility!!.state == BottomSheetBehavior.STATE_HALF_EXPANDED ||
            mBottomSheetBehaviorMobility!!.state == BottomSheetBehavior.STATE_COLLAPSED
        ){
            mBottomSheetBehaviorMobility!!.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else{
            mBottomSheetBehaviorMobility!!.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bindingMainActivity.bottomSheetUser.fullname.text = "   $fullname"
        bindingMainActivity.bottomSheetUser.email.text = "   $email"
    }

    private fun showChoixMenuDialog() {
        val dialog = Dialog(this@MainActivity, R.style.Theme_D1NoTitleDim)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        dialog.setCancelable(true)
        val bindingDialog = DialogChoixMenuBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.unscribe.setOnClickListener {
            SharedPreferenceManager.getInstance(this@MainActivity)!!.clear()
            finish()
            dialog.dismiss()
        }

        bindingDialog.logout.setOnClickListener {
            val home = Intent(this, LoginActivity::class.java)
            startActivity(home)
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        // Position par défaut (par exemple, le centre de lome)
        val defaultLocation = LatLng(6.175691, 1.214857)
        val marker = googleMap.addMarker(MarkerOptions().position(defaultLocation).title("Marqueur par défaut"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Vérifiez les autorisations de localisation
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Demandez les mises à jour de localisation
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Mettez à jour la position du marqueur avec la nouvelle localisation
                location?.let {
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    marker!!.position = newLatLng
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng))
                }
            }
        } else {
            // Demandez la permission de localisation si elle n'est pas accordée
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Activer la couche "My Location" pour afficher le point bleu actuel de l'utilisateur
        googleMap.isMyLocationEnabled = true
    }
}