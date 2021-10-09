package com.example.messanger.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.messanger.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    @SuppressLint("WrongConstant", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initViews()

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment? ?: return

        navController = host.navController
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when (destination.id) {
                R.id.loginFragment -> bottomNav.isVisible = false

                R.id.registerFragment -> {
                    bottomNav.isVisible = false
                }

                R.id.messageFragment -> {
                    bottomNav.isVisible = false
                }

                else -> {
                    bottomNav.isVisible = true

                }
            }
        }
    }


    private fun initViews() {
        bottomNav = findViewById(R.id.bottom_nav)

    }


}


