package com.mcwilliams.streak.strava.model.activitydetail

import com.mcwilliams.streak.strava.model.activitydetail.Athlete
import com.mcwilliams.streak.strava.model.activitydetail.Gear
import com.mcwilliams.streak.strava.model.activitydetail.HighlightedKudoser
import com.mcwilliams.streak.strava.model.activitydetail.Lap
import com.mcwilliams.streak.strava.model.activitydetail.Map
import com.mcwilliams.streak.strava.model.activitydetail.Photos
import com.mcwilliams.streak.strava.model.activitydetail.SegmentEffort
import com.mcwilliams.streak.strava.model.activitydetail.SplitsMetric
import java.io.Serializable

data class StravaActivityDetail(
    val achievement_count: Int,
    val athlete: Athlete,
    val athlete_count: Int,
    val average_cadence: Double,
    val average_speed: Double,
    val average_temp: Int,
    val average_watts: Double,
    val calories: Double,
    val comment_count: Int,
    val commute: Boolean,
    val description: String,
    val device_name: String,
    val device_watts: Boolean,
    val distance: Float,
    val average_heartrate: Double,
    val elapsed_time: Int,
    val elev_high: Double,
    val elev_low: Double,
    val embed_token: String,
    val end_latlng: List<Double>,
    val external_id: String,
    val flagged: Boolean,
    val from_accepted_tag: Boolean,
    val gear: Gear,
    val gear_id: String,
    val has_heartrate: Boolean,
    val has_kudoed: Boolean,
    val highlighted_kudosers: List<HighlightedKudoser>,
    val id: Long,
    val kilojoules: Double,
    val kudos_count: Int,
    val laps: List<Lap>,
    val leaderboard_opt_out: Boolean,
    val manual: Boolean,
    val map: Map?,
    val max_speed: Double,
    val max_watts: Int,
    val moving_time: Int,
    val name: String,
    val partner_brand_tag: Any,
    val photo_count: Int,
    val photos: Photos,
    val pr_count: Int,
    val `private`: Boolean,
    val resource_state: Int,
    val segment_efforts: List<SegmentEffort>,
    val segment_leaderboard_opt_out: Boolean,
    val splits_metric: List<SplitsMetric>?,
    val splits_standard: List<SplitsMetric>?,
    val start_date: String,
    val start_date_local: String,
    val start_latlng: List<Double>,
    val suffer_score: Any,
    val timezone: String,
    val total_elevation_gain: Double,
    val total_photo_count: Int,
    val trainer: Boolean,
    val type: String,
    val upload_id: Long,
    val utc_offset: Int,
    val weighted_average_watts: Int,
    val workout_type: Int,

    var miles: String,
    var formattedDate: String,
    var duration: String

) : Serializable