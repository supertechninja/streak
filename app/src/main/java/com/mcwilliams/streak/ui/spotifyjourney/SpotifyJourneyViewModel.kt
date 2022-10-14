package com.mcwilliams.streak.ui.spotifyjourney

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcwilliams.streak.inf.spotify.SpotifyApis
import com.mcwilliams.streak.inf.spotify.SpotifySessionRepository
import com.mcwilliams.streak.inf.spotify.model.RecentlyPlayedSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotifyJourneyViewModel @Inject constructor(
    private val spotifySessionRepository: SpotifySessionRepository,
    private val spotifyApis: SpotifyApis
) : ViewModel() {

    private var _isLoggedInSpotify: MutableLiveData<Boolean> = MutableLiveData(null)
    var isLoggedInSpotify: LiveData<Boolean> = _isLoggedInSpotify

    private var _recentlyPlayedSongs: MutableLiveData<RecentlyPlayedSongs> = MutableLiveData(null)
    var recentlyPlayedSongs: LiveData<RecentlyPlayedSongs> = _recentlyPlayedSongs

    init {
        _isLoggedInSpotify.postValue(spotifySessionRepository.isLoggedIn())
    }

    fun saveCode(spotifyCode: String?) {
        spotifyCode?.let {
            viewModelScope.launch {
                val response = spotifySessionRepository.getFirstTokens(it)

                _isLoggedInSpotify.postValue(spotifySessionRepository.isLoggedIn())
            }
        }
    }

    fun getRecentlyPlayedSongs(){
        viewModelScope.launch {
            val recentlyPlayedSongs = spotifyApis.getRecentlyPlayedSongs()
            if(recentlyPlayedSongs.isSuccessful){
                _recentlyPlayedSongs.postValue(recentlyPlayedSongs.body())
            }
        }
    }
}