package com.mcwilliams.streak.inf.spotify

import android.content.Context
import androidx.annotation.Keep

@Keep
class SpotifyAuthorize() {
    private var clientID = ""
    private var redirectURI: String? = null
    private var state: String? = null
    private var accessScope: String? = null

    fun withClientID(clientID: String): SpotifyAuthorize {
        this.clientID = clientID
        return this
    }

    fun withRedirectURI(redirectURI: String?): SpotifyAuthorize {
        this.redirectURI = redirectURI
        return this
    }

    fun withState(state: String?): SpotifyAuthorize {
        this.state = state
        return this
    }

    fun withAccessScope(accessScope: String?): SpotifyAuthorize {
        this.accessScope = accessScope
        return this
    }

    fun makeLoginURL(): String {
        val loginURLBuilder = StringBuilder()
        loginURLBuilder.append(STRAVA_LOGIN_URL)
        loginURLBuilder.append(clientIDParameter())
        loginURLBuilder.append(redirectURIParameter())
        loginURLBuilder.append(stateParameter())
        loginURLBuilder.append(accessScopeParameter())
        return loginURLBuilder.toString()
    }

    private fun clientIDParameter(): String {
        return "&client_id=$clientID"
    }

    private fun redirectURIParameter(): String {
        return if (redirectURI != null) {
            "&redirect_uri=$redirectURI"
        } else {
            ""
        }
    }

    private fun stateParameter(): String {
        return if (state != null) {
            "&state=" + state.toString()
        } else {
            ""
        }
    }

    private fun accessScopeParameter(): String {
        return if (accessScope != null) {
            "&scope=" + accessScope.toString()
        } else {
            ""
        }
    }

    companion object {
        private const val STRAVA_LOGIN_URL =
            "https://accounts.spotify.com/authorize?response_type=code"
    }
}