package com.mcwilliams.streak.strava.model.activites.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem

@Dao
interface ActivitiesDao {
    @Query("SELECT * FROM activitiesItem ")
    fun getAll(): List<ActivitiesItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: ActivitiesItem)

    @Query("SELECT * FROM activitiesItem LIMIT 2")
    fun getLast10Activities() : List<ActivitiesItem>

}