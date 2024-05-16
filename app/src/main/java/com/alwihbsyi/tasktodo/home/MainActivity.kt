package com.alwihbsyi.tasktodo.home

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alwihbsyi.tasktodo.R
import com.alwihbsyi.tasktodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavigation()

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ), 303
            )
        }
    }

    private fun setUpNavigation() {
        binding.bottomNavigationView.background = null
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentLayout.id) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.selectedItemId = R.id.homeFragment
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}