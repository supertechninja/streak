package com.mcwilliams.streak.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.mcwilliams.streak.inf.spotify.SpotifyAuthorize
import com.mcwilliams.streak.ui.dashboard.ActivityType
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel
import com.mcwilliams.streak.ui.dashboard.UnitType


@ExperimentalComposeUiApi
@Composable
fun StreakSettingsView(
    viewModel: StravaDashboardViewModel,
    selectedActivityType: ActivityType?,
    selectedUnitType: UnitType?,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        },
        content = {
            var showSpotifyAuthentication by remember { mutableStateOf(false) }

            val context = LocalContext.current

            Box() {

                Column(
                    modifier = Modifier
                        .padding(it)
                        .background(color = MaterialTheme.colorScheme.surface)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Activities",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                            ) {
                                ActivityType.values().forEach { activityType ->
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.updateSelectedActivity(activityType = activityType)
                                            }
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .height(40.dp),

                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            activityType.name,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        selectedActivityType?.let {
                                            if (it.name == activityType.name) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Currently Selected",
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Units",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.updateSelectedUnit(UnitType.Imperial)
                                        }
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(40.dp),

                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Imperial", color = MaterialTheme.colorScheme.onSurface)

                                    if (UnitType.Imperial.name == selectedUnitType?.name) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Currently Selected",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.updateSelectedUnit(UnitType.Metric)
                                        }
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(40.dp),

                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Metric", color = MaterialTheme.colorScheme.onSurface)

                                    if (UnitType.Metric.name == selectedUnitType?.name) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Currently Selected",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                showSpotifyAuthentication = true
//                                openTab(context, loadLoginUrl(context))


//                                val connection: CustomTabsServiceConnection =
//                                    object : CustomTabsServiceConnection() {
//                                        override fun onCustomTabsServiceConnected(
//                                            name: ComponentName,
//                                            client: CustomTabsClient
//                                        ) {
//                                            val builder = CustomTabsIntent.Builder()
//                                            val intent = builder.build()
//                                            client.warmup(0L) // This prevents backgrounding after redirection
//                                            intent.launchUrl(context, Uri.parse(loadLoginUrl(context)))
//                                        }
//
//                                        override fun onServiceDisconnected(name: ComponentName?) {
//
//                                        }
//                                    }
//
//                                CustomTabsClient.bindCustomTabsService(
//                                    context,
//                                    "com.android.chrome",
//                                    connection
//                                )

//                                val customTabsIntent = CustomTabsIntent.Builder().build()
//                                val packageName: String = context.packageName
//                                customTabsIntent.intent.setPackage(packageName)
//                                val theLocationUri = Uri.parse()
//                                customTabsIntent.launchUrl(context, theLocationUri)
                            }) {
                                Text(text = "Link Spotify")
                            }
                        }
                    }


                    val widgetStatus by viewModel.widgetStatus
                    if (widgetStatus) {
                        Log.d("TAG", "StreakSettingsView: TRUE")
                    }

                    Box(
                        modifier = Modifier
                            .padding(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        androidx.compose.material3.Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Log out")
                        }
//                    Button(
//                        onClick = { viewModel.logout() },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
//                    ) {
//                        Text(text = "Log out", color = MaterialTheme.colorScheme.onPrimary)
//                    }
                    }
                }

                if (showSpotifyAuthentication) {

                    Dialog(onDismissRequest = {
                        showSpotifyAuthentication = !showSpotifyAuthentication
                    }) {
                        val webViewState  = rememberWebViewState(loadLoginUrl(context))

                        val client = object : AccompanistWebViewClient(){
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return if (request?.url.toString().startsWith("https://www.streakapp.com/authorize/")) {
                                    val code = request?.url?.getQueryParameter("code")
                                    viewModel.saveCode(request?.url?.getQueryParameter("code"))
                                    Log.d("TAG", "shouldOverrideUrlLoading: $code")
                                    showSpotifyAuthentication = false
                                    true
                                } else {
                                    false
                                }
                            }
                        }

                        WebView(
                            state = webViewState,
                            onCreated = { it.settings.javaScriptEnabled = true },
                            client = client
                        )
                    }
                }
            }
        }
    )
}

private fun loadLoginUrl(context: Context): String = SpotifyAuthorize()
    .withClientID("0fb6298f96e24dc8a4b80a1109522ef9")
    .withRedirectURI("https://www.streakapp.com/authorize/")
    .withAccessScope("user-read-recently-played")
    .makeLoginURL()