package com.mcwilliams.streak.strava.model.activites

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(indices = [Index(value = ["start_date"], unique = true)])
data class ActivitiesItem(
    @ColumnInfo(name = "distance") val distance: Float,
    @ColumnInfo(name = "calories") val calories: Double?,
    @ColumnInfo(name = "elapsed_time") val elapsed_time: Int,
    @PrimaryKey(autoGenerate = true) val activityId: Int,
    @ColumnInfo(name = "moving_time") val moving_time: Int,
    @ColumnInfo(name = "start_date") val start_date: String,
    @ColumnInfo(name = "start_date_local") val start_date_local: String,
    @ColumnInfo(name = "total_elevation_gain") val total_elevation_gain: Float,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "id") val id: Long
)