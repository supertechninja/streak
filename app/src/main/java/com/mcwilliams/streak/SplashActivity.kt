package com.mcwilliams.streak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val viewModel: StravaDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.isLoggedIn.observe(this, {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isLoggedIn", it)
            startActivity(intent)
        })


    }
}