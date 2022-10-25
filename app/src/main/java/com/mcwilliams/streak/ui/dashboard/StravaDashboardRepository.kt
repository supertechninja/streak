package com.mcwilliams.streak.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.api.ActivitiesApi
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.strava.model.activites.db.ActivitiesDao
import com.mcwilliams.streak.strava.model.activites.db.ActivitiesDatabase
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StravaDashboardRepository @Inject constructor(
    val context: Context,
    private val activitiesApi: ActivitiesApi
): SharedPreferences.OnSharedPreferenceChangeListener {

    //Cache in memory the strava workouts
    private lateinit var listOfStravaWorkouts: List<ActivitiesItem>

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    private var activitiesDao: ActivitiesDao?

    private val _widgetStatus = MutableSharedFlow<Boolean>(replay = 0)
    val widgetStatus: SharedFlow<Boolean> = _widgetStatus

    fun refreshPrefs(): Boolean {
        return preferences.getBoolean("widgetEnable", false)
    }

    init {
        val db = ActivitiesDatabase.getDatabase(context)
        activitiesDao = db?.activitiesDao()

        preferences.registerOnSharedPreferenceChangeListener(this)
        _widgetStatus.tryEmit(refreshPrefs())
    }

    //    val store = StoreBuilder
//        .from(
//            fetcher = Fetcher.of {
//                activitiesApi.fetchSubreddit(
//                    it,
//                    "10"
//                ).data.children.map(::toPosts)
//            },
//            sourceOfTruth = SourceOfTruth.of(
//                reader = db.postDao()::loadPosts,
//                writer = db.postDao()::insertPosts,
//                delete = db.postDao()::clearFeed,
//                deleteAll = db.postDao()::clearAllFeeds
//            )
//        ).build()

    suspend fun getRecentActivities(): List<ActivitiesItem> {
        var allActivities: List<ActivitiesItem>

        withContext(Dispatchers.IO) {
            allActivities = activitiesDao?.getLast10Activities() ?: emptyList()
        }

        return allActivities
    }
    fun loadActivities(
        before: Int? = null,
        after: Int? = null,
    ): Flow<List<ActivitiesItem>> = flow {
        var allActivities: List<ActivitiesItem>?

        //query db
        withContext(Dispatchers.IO) {
            allActivities = activitiesDao?.getAll()

        }

        var beforeDate = before
        var afterDate = after

        val lastUpdated = fetchLastUpdatedTime()
        var shouldCallApi = false

        if (lastUpdated != null) {
            val currentTime = LocalDateTime.now()
            if (currentTime > lastUpdated) {
                //Date outdated refreshing
                Log.d("TAG", "loadActivities: DATA OUTDATED")
                shouldCallApi = true
                beforeDate = getEpoch(
                    currentTime.year,
                    currentTime.monthValue - 1,
                    currentTime.dayOfMonth,
                    currentTime.hour,
                    currentTime.minute
                ).first
                Log.d("TAG", "Before Current: $currentTime")

                if (allActivities != null) {
                    //get most recently stored activity to determine the "after date" to call the api
                    val date = allActivities!!.minByOrNull {
                        kotlin.math.abs(
                            it.start_date.getDate().atStartOfDay()
                                .toEpochSecond(ZoneOffset.UTC) - currentTime.toEpochSecond(
                                ZoneOffset.UTC
                            )
                        )
                    }?.start_date?.getDateTime()
                    Log.d("TAG", "loadActivities: $date")

                    date?.let {
                        afterDate = getEpoch(
                            it.year,
                            it.monthValue - 1,
                            it.dayOfMonth,
                            it.hour,
                            it.minute
                        ).first

                        Log.d("TAG", "After: $it")
                    }
                }
            }
        }

        //Check db
        if (allActivities.isNullOrEmpty() || shouldCallApi) {
            val remoteActivities: Flow<List<ActivitiesItem>> = flow {
                val remote = getStravaActivitiesBeforeAndAfterPaginated(beforeDate, afterDate)

                remote.collect {
                    allActivities = it
                    emit(allActivities!!)
                }
            }

            remoteActivities.collect {
                shouldCallApi = false
                saveLastFetchTimestamp()
                withContext(Dispatchers.IO) {
                    allActivities = activitiesDao?.getAll()

                }
                emit(allActivities!!)
                Log.d("TAG", "loadActivities: FETCHED REFRESHING FROM DB")
                //reload from DB
            }

        } else {
            emit(allActivities!!)
        }
    }


    fun getStravaActivitiesBeforeAndAfterPaginated(
        before: Int?,
        after: Int?
    ): Flow<List<ActivitiesItem>> = flow {
        val yearActivities: MutableList<ActivitiesItem> = mutableListOf()
        var pageCount = 1
        do {
            yearActivities.addAll(
                activitiesApi.getAthleteActivitiesBeforeAndAfter(
                    before = before,
                    after = after,
                    page = pageCount
                )
            )
            pageCount = pageCount.inc()

            //Add activities to db
            yearActivities.map {
                runBlocking {
                    saveActivty(it)
                }
            }

        } while (yearActivities.size % 200 == 0 && yearActivities.size != 0)

        emit(yearActivities)
    }

    //Write activity to db
    suspend fun saveActivty(activitiesItem: ActivitiesItem) {
        withContext(Dispatchers.IO) {
            activitiesDao?.insertAll(activitiesItem)
        }
    }

    fun savePreferredActivity(activityType: ActivityType) {
        preferences.edit().putString(activityTypeKey, activityType.name).apply()
    }

    fun getPreferredActivity() =
        preferences.getString(activityTypeKey, ActivityType.Run.name)?.let {
            ActivityType.valueOf(
                it
            )
        }

    fun savePreferredUnits(unitType: UnitType) {
        preferences.edit().putString(unitTypeKey, unitType.name).apply()
    }

    fun getPreferredUnitType() = preferences.getString(unitTypeKey, UnitType.Imperial.name)?.let {
        UnitType.valueOf(
            it
        )
    }

    fun saveLastFetchTimestamp() {
        val currentTime = LocalDateTime.now()
        Log.d("TAG", "saveLastFetchTimestamp: $currentTime")
        preferences.edit().putString(lastUpdatedKey, currentTime.toString()).apply()
    }

    fun fetchLastUpdatedTime(): LocalDateTime? {
        val lastUpdatedString = preferences.getString(lastUpdatedKey, "")
        return if (lastUpdatedString.isNullOrEmpty())
            null
        else
            LocalDateTime.parse(lastUpdatedString)
    }

    fun saveWeeklyDistance(weeklyDistance: String, weeklyElevation: String) {
        preferences.edit().putString("weeklyDistance", weeklyDistance).apply()
        preferences.edit().putString("weeklyElevation", weeklyElevation).apply()
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        if (key == "widgetEnabled") {
            val widgetEnabled = preferences?.getBoolean("widgetEnable", false)
            widgetEnabled?.let {
                if (it) {
                    Log.d("TAG", "onSharedPreferenceChanged: $it")
                    _widgetStatus.tryEmit(refreshPrefs())
                }
            }
        }
    }

    companion object {
        const val activityTypeKey: String = "activityType"
        const val unitTypeKey: String = "unitType"
        const val lastUpdatedKey: String = "lastUpdated"
    }
}