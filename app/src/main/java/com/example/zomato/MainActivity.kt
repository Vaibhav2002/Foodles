package com.example.zomato

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zomato.databinding.ActivityMainBinding
import com.example.zomato.util.LOCATION_REQUEST_CODE
import com.example.zomato.util.LocationHelper
import com.example.zomato.util.adUnitID
import com.example.zomato.util.testAdId
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var interstitialAd: InterstitialAd
    private val rationale = "This app needs to have access to your location to fetch restaurants"
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.show()
        setContentView(binding.root.rootView)
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.nearbyFragment,
                R.id.collectionsFragment,
                R.id.searchFragment
            )
            .build()

        navController = findNavController(R.id.fragment_host)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.restaurantDetailFragment -> supportActionBar?.hide()
                else -> supportActionBar?.show()
            }
        }
        getLocation()

        LocationHelper(this).isConnected().observe(this, {
            if (it) {
                showViewsForSuccess()
            } else {
                showViewsForLocationError()
            }
        })

        MobileAds.initialize(this)
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = testAdId
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }

            override fun onAdFailedToLoad(p0: Int) {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        EasyPermissions.requestPermissions(
            this, rationale, LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun getLocation() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            val locationHelper = LocationHelper(this)
            locationHelper.attachObserver { location ->
                viewModel.setLocation(location)
            }
            locationHelper.startListening()
        } else EasyPermissions.requestPermissions(
            this,
            "",
            LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun showViewsForLocationError() {
        binding.content.isVisible = false
        binding.locationError.isVisible = true
    }

    fun showViewsForSuccess() {
        binding.content.isVisible = true
        binding.locationError.isVisible = false
    }
}