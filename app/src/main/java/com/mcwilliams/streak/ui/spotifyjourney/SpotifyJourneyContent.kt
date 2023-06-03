package com.mcwilliams.streak.ui.spotifyjourney

import android.content.Context
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.mcwilliams.streak.inf.spotify.SpotifyAuthorize

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun SpotifyJourneyContent() {
    val spotifyJourneyViewModel = hiltViewModel<SpotifyJourneyViewModel>()

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
                    "Journey",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        },
        content = {
            val isLoggedIn by spotifyJourneyViewModel.isLoggedInSpotify.observeAsState()

            val recentlyPlayedSongs by spotifyJourneyViewModel.recentlyPlayedSongs.observeAsState()

            var showSpotifyAuthentication by remember { mutableStateOf(false) }

            val context = LocalContext.current

            Box(modifier = Modifier.padding(it), contentAlignment = Alignment.Center) {

                if (isLoggedIn == true) {
                    spotifyJourneyViewModel.getRecentlyPlayedSongs()

                    recentlyPlayedSongs?.let {
                        LazyColumn() {
                            items(it) {
                                val activity = it.first

                                val trackList = it.second

                                Text(
                                    "Activity: ${activity.type}",
                                    style = MaterialTheme.typography.displayMedium,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )

                                Text("Songs Listened to:")

                                trackList.forEach {
                                    Text("- ${it.name.orEmpty()}")
                                }

                            }
                        }
                    }
                } else {
                    Button(onClick = {
                        showSpotifyAuthentication = true
                    }) {
                        Text(text = "Link Spotify")
                    }
                }

                if (showSpotifyAuthentication) {
                    Dialog(onDismissRequest = {
                        showSpotifyAuthentication = !showSpotifyAuthentication
                    }) {
                        val webViewState = rememberWebViewState(loadLoginUrl(context))

                        val client = object : AccompanistWebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return if (request?.url.toString()
                                        .startsWith("https://www.streakapp.com/authorize/")
                                ) {
                                    val code = request?.url?.getQueryParameter("code")
                                    spotifyJourneyViewModel.saveCode(
                                        request?.url?.getQueryParameter(
                                            "code"
                                        )
                                    )
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