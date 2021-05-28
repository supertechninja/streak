package com.mcwilliams.streak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val viewModel: StravaDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.isLoggedIn.observe(this, {
            it?.let {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("isLoggedIn", it)
                startActivity(intent)
            }
        })


    }
}