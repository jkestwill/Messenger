package com.example.messanger.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.messanger.App
import com.example.messanger.R
import com.example.messanger.databinding.ActivityMainBinding
import com.example.messanger.viewmodels.MainViewModel
import com.example.messanger.viewmodels.ViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelProviderFactory

    private val viewModel: MainViewModel by viewModels<MainViewModel>() {
        factory
    }

    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeNavigationChanges()

        viewModel.messageCountLiveDate.observe(this) {
            if (it != 0)
                binding.bottomNav.getOrCreateBadge(R.id.messenger).number = it
            else
                binding.bottomNav.removeBadge(R.id.messenger)
        }


        viewModel.notificationCountLiveData.observe(this) {
            if (it != 0)
                binding.bottomNav.getOrCreateBadge(R.id.notification).number = it
            else
                binding.bottomNav.removeBadge(R.id.notification)

        }
    }


    private fun observeNavigationChanges() {

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment? ?: return

        val navController = host.navController
        binding.bottomNav.setupWithNavController(navController)
        // отслеживает в каком фрагменте находится пользователь
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when (destination.id) {
                R.id.loginFragment -> {
                    binding.bottomNav.isVisible = false
                }

                R.id.registerFragment -> {
                    binding.bottomNav.isVisible = false
                }

                R.id.messageFragment -> {
                    binding.bottomNav.isVisible = false
                }

                else -> {
                    viewModel.getMessageCount()
                    viewModel.notificationCount()
                    binding.bottomNav.isVisible = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }


}


