package com.mcwilliams.streak.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mcwilliams.streak.R
import com.mcwilliams.streak.strava.api.ActivitiesApi
import com.mcwilliams.streak.strava.model.activites.ActivitiesItem
import com.mcwilliams.streak.strava.model.activites.db.ActivitiesDao
import com.mcwilliams.streak.strava.model.activites.db.ActivitiesDatabase
import com.mcwilliams.streak.ui.dashboard.ActivityType.All
import com.mcwilliams.streak.ui.dashboard.ActivityType.Run
import com.mcwilliams.streak.ui.dashboard.ActivityType.valueOf
import com.mcwilliams.streak.ui.dashboard.data.CalendarActivities
import com.mcwilliams.streak.ui.dashboard.data.CalendarData
import com.mcwilliams.streak.ui.dashboard.data.getEpoch
import com.mcwilliams.streak.ui.utils.getDate
import com.mcwilliams.streak.ui.utils.getDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StravaDashboardRepository @Inject constructor(
    val context: Context,
    private val activitiesApi: ActivitiesApi
) : SharedPreferences.OnSharedPreferenceChangeListener {

    //Cache in memory the strava workouts
    private lateinit var listOfStravaWorkouts: List<ActivitiesItem>

    private val preferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    private var activitiesDao: ActivitiesDao?

    private val _widgetStatus = MutableSharedFlow<Boolean>(replay = 0)
    val widgetStatus: SharedFlow<Boolean> = _widgetStatus

    val calendarData = CalendarData()

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
    ): Flow<CalendarActivities> = flow {
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
                shouldCallApi = true
                beforeDate = getEpoch(
                    currentTime.year,
                    currentTime.monthValue - 1,
                    currentTime.dayOfMonth,
                    currentTime.hour,
                    currentTime.minute
                ).first

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

                    date?.let {
                        afterDate = getEpoch(
                            it.year,
                            it.monthValue - 1,
                            it.dayOfMonth,
                            it.hour,
                            it.minute
                        ).first
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
            }

        }

        allActivities?.let { activities ->

            var currentYearActivities: List<ActivitiesItem> =
                activities.filter { it.start_date.getDate().year == calendarData.currentYearInt }
            var previousYearActivities: List<ActivitiesItem> =
                activities.filter { it.start_date.getDate().year == calendarData.currentYearInt - 1 }
            var twoYearsAgoActivities: List<ActivitiesItem> =
                activities.filter { it.start_date.getDate().year == calendarData.currentYearInt - 2 }


            var currentMonthActivities: List<ActivitiesItem> =
                currentYearActivities.filter { it.start_date.getDate().monthValue == calendarData.currentMonthInt }

            var previousMonthActivities: List<ActivitiesItem> = currentYearActivities.filter {
                if (calendarData.currentMonthInt == 1) {
                    it.start_date.getDate().monthValue == 12
                            && it.start_date.getDate().year == 2021
                } else {
                    it.start_date.getDate().monthValue == calendarData.currentMonthInt - 1
                            && it.start_date.getDate().year == 2022
                }
            }

            var twoMonthAgoActivities: List<ActivitiesItem> = currentYearActivities.filter {
                if (calendarData.currentMonthInt == 1) {
                    it.start_date.getDate().monthValue == 11
                            && it.start_date.getDate().year == 2021
                } else {
                    it.start_date.getDate().monthValue == calendarData.currentMonthInt - 2
                            && it.start_date.getDate().year == 2022
                }
            }

            emit(
                CalendarActivities(
                    currentMonthActivities = currentMonthActivities,
                    previousMonthActivities = previousMonthActivities,
                    twoMonthAgoActivities = twoMonthAgoActivities,
                    currentYearActivities = currentYearActivities,
                    previousYearActivities = previousYearActivities,
                    twoYearsAgoActivities = twoYearsAgoActivities,
                    preferredActivityType = getPreferredActivity(),
                    selectedUnitType = getPreferredUnitType()
                )
            )
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
        preferences.getString(activityTypeKey, Run.name)?.let {
            valueOf(
                it
            )
        } ?: All

    fun savePreferredUnits(unitType: UnitType) {
        preferences.edit().putString(unitTypeKey, unitType.name).apply()
    }

    fun getPreferredUnitType() = preferences.getString(unitTypeKey, UnitType.Imperial.name)?.let {
        UnitType.valueOf(
            it
        )
    } ?: UnitType.Imperial

    fun saveLastFetchTimestamp() {
        val currentTime = LocalDateTime.now()
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