package com.mcwilliams.streak.ui.spotifyjourney

import android.util.Log
import com.mcwilliams.streak.inf.spotify.SpotifyApis
import com.mcwilliams.streak.inf.spotify.model.Track
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.ui.dashboard.StravaDashboardRepository
import com.mcwilliams.streak.ui.dashboard.toMillis
import com.mcwilliams.streak.ui.utils.getDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyJourneyRepository @Inject constructor(
    private val spotifyApis: SpotifyApis,
    private val stravaDashboardRepository: StravaDashboardRepository
) {
    suspend fun getRecentlyPlayed(): MutableList<Pair<ActivitiesItem, MutableList<Track>>>? {
        val recentActivities = stravaDashboardRepository.getRecentActivities()

        val songMap = mutableListOf<Pair<ActivitiesItem, MutableList<Track>>>()

        for (activity in recentActivities) {
            val startEpochSec =
                activity.start_date_local.getDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()

            Log.d(
                "TAG",
                "getRecentlyPlayed: START: ${activity.start_date_local.getDateTime()} milli: $startEpochSec"
            )

            val endEpochSec = startEpochSec.plus(activity.elapsed_time.times(1000))

            Log.d("TAG", "getRecentlyPlayed: END: $endEpochSec")

            val getSongsForWorkout = spotifyApis.getRecentlyPlayedSongs(endEpochSec)

            if (getSongsForWorkout.isSuccessful) {

                val songsForActivity = mutableListOf<Track>()

                getSongsForWorkout.body()?.let { songs ->
                    for (song in songs.items.mapNotNull { it }) {

                        val songStarted =
                            song.playedAt?.getDateTime()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

                        songStarted?.let {
                            Log.d("TAG", "getRecentlyPlayed: track started: ${song.playedAt}, $it workout end: $endEpochSec")
                            if (it > startEpochSec) {
                                song.track?.let { track ->
                                    songsForActivity.add(track)
                                }
                            }
                        }
                    }
                }

                songMap.add(activity to songsForActivity)
            }
        }

        return songMap
    }
}