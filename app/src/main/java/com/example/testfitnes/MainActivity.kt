package com.example.testfitnes

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.presentation.network.NetworkConnectionLiveData
import com.example.presentation.network.NoInternetFragment
import com.example.testfitnes.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noInternetLiveData: NetworkConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        checkNetwork()
    }

    private fun checkNetwork() {
        noInternetLiveData = NetworkConnectionLiveData(this)

        noInternetLiveData.observe(this) { isConnected ->
            val fragmentManager = supportFragmentManager
            val existing = fragmentManager.findFragmentByTag("no_internet")

            if (!isConnected && existing == null && !fragmentManager.isStateSaved) {
                fragmentManager.beginTransaction()
                    .add(android.R.id.content, NoInternetFragment(), "no_internet")
                    .commitNowAllowingStateLoss()
            } else if (isConnected && existing != null) {
                fragmentManager.beginTransaction()
                    .remove(existing)
                    .commitNowAllowingStateLoss()
            }
        }
    }
}

