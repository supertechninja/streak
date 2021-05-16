package com.mcwilliams.streak.ui.settings

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mcwilliams.streak.inf.model.StravaLogin
import com.mcwilliams.streak.ui.dashboard.StravaDashboardViewModel

@Composable
fun StravaAuthWebView(viewModel: StravaDashboardViewModel, onFinish: () -> Unit) {
    AndroidView(factory = { context ->
        val loginWebview = WebView(context)
        loginWebview.settings.javaScriptEnabled = true
        loginWebview.settings.userAgentString = "Mozilla/5.0 Google"
        loginWebview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return handleUrl(Uri.parse(url)) || super.shouldOverrideUrlLoading(view, url)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val uri = request.url
                return handleUrl(uri) || super.shouldOverrideUrlLoading(view, request)
            }

            private fun handleUrl(uri: Uri): Boolean {
                if (uri.toString().startsWith("https://www.streakstravaapp.com")) {
                    val code = uri.getQueryParameter("code")
                    return makeResult(code)
                }
                return false
            }

            private fun makeResult(code: String?): Boolean {
                if (code != null && code.isNotEmpty()) {
                    loginWebview.visibility = View.GONE

                    viewModel.loginAthlete(code!!)
                    onFinish()
                    return true
                }
                return false
            }
        }

        loginWebview.loadUrl(loadLoginUrl(context))
        return@AndroidView loginWebview

    }, modifier = Modifier.fillMaxSize())
}

private fun loadLoginUrl(context: Context): String = StravaLogin.withContext(context)
    .withClientID(66172)
    .withRedirectURI("https://www.streakstravaapp.com")
    .withApprovalPrompt("auto")
    .withAccessScope("activity:read,activity:write")
    .makeLoginURL()