package com.mcwilliams.streak.ui.dashboard.data

import android.util.Log
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale

data class CalendarData(
    val currentDateTime: LocalDate = LocalDate.now(),
    val currentMonthInt: Int = currentDateTime.monthValue,
    val currentDayInt: Int = currentDateTime.dayOfMonth,
    val currentYearInt: Int = currentDateTime.year,
    val currentDayOfYeah: Int = currentDateTime.dayOfYear,

    val currentMonth: Pair<Int, String> = getEpoch(LocalDate.now().year, currentMonthInt - 1, 1),
    val previousMonth: Pair<Int, String> = getEpoch(LocalDate.now().year, currentMonthInt - 2, 1),
    val twoMonthPrevious: Pair<Int, String> = getEpoch(
        LocalDate.now().year,
        currentMonthInt - 3,
        1
    ),

    val currentYear: Pair<Int, String> = getEpoch(
        year = LocalDate.now().year,
        month = currentMonthInt - 1,
        LocalDate.now().dayOfMonth
    ).first to "2022",
    val prevYear: Pair<Int, String> = getEpoch(
        year = LocalDate.now().year - 1,
        month = 0,
        1
    ).first to "2021",
    val twoYearsAgo: Pair<Int, String> = getEpoch(
        year = LocalDate.now().year - 2,
        month = 0,
        1
    ).first to "2020",

    var currentWeek : MutableList<Pair<Int, Int>> = mutableListOf(),
){
    val monthWeekMap : MutableMap<Int, MutableList<Pair<Int, Int>>> = monthBreakDown()

    private fun monthBreakDown(): MutableMap<Int, MutableList<Pair<Int, Int>>> {
        val monthWeekMap: MutableMap<Int, MutableList<Pair<Int, Int>>> = mutableMapOf()
        val month = YearMonth.now()
        val firstDayOffset = month.atDay(1).dayOfWeek.ordinal
        val monthLength = month.lengthOfMonth()
        val currentMonth = YearMonth.now().month

        val priorMonthLength = month.minusMonths(1).lengthOfMonth()
        val lastDayCount = (monthLength + firstDayOffset) % 7
        val weekCount = (firstDayOffset + monthLength) / 7

        for (week in 0..weekCount) {
            val listOfDatesInWeek: MutableList<Pair<Int, Int>> = mutableListOf()

            if (week == 0) {
                for (i in 0 until firstDayOffset) {
                    val priorDay = (priorMonthLength - (firstDayOffset - i - 1))

                    val month = if(currentMonth.value == 1) 12 else currentMonth.value - 1
                    listOfDatesInWeek.add(month to priorDay)
                }
            }

            val endDay = when (week) {
                0 -> 7 - firstDayOffset
                weekCount -> lastDayCount
                else -> 7
            }

            for (i in 1..endDay) {
                val day =
                    if (week == 0) i else (i + (7 * week) - firstDayOffset)

                listOfDatesInWeek.add(currentMonth.value to day)
            }

            listOfDatesInWeek.forEach { weekDates ->
                if (weekDates.second == LocalDate.now().dayOfMonth) {
                    currentWeek = listOfDatesInWeek
                }
            }

            monthWeekMap.put(week, listOfDatesInWeek)
        }

        val previousMonthInt = if(currentMonth.value == 1) 12 else currentMonth.value - 1

        //Add previous 2 weeks to week map
        val firstDayWeekZeroMonth =
            (priorMonthLength - (firstDayOffset - 1))

        val listOfDatesInPreviousWeek: MutableList<Pair<Int, Int>> =
            mutableListOf()

        for (i in 0..6) {
            if (currentDayInt < 7) {
                val priorDay = (firstDayWeekZeroMonth - (i + 1))
                listOfDatesInPreviousWeek.add(previousMonthInt to priorDay)
            } else {
                val priorDay = (firstDayWeekZeroMonth - (i + 1))
                listOfDatesInPreviousWeek.add(currentMonth.value to priorDay)
            }
        }
        monthWeekMap.put(-1, listOfDatesInPreviousWeek)

        val listOfDatesInTwoWeeksAgo: MutableList<Pair<Int, Int>> =
            mutableListOf()
        val twoWeekAgo = firstDayWeekZeroMonth - 7
        for (i in 0..6) {
            if (currentDayInt < 7) {
                val priorDay = (twoWeekAgo - (i + 1))
                listOfDatesInTwoWeeksAgo.add(previousMonthInt to priorDay)
            } else {
                val priorDay = (firstDayWeekZeroMonth - (i + 1))
                listOfDatesInTwoWeeksAgo.add(currentMonth.value to priorDay)
            }
        }
        monthWeekMap.put(-2, listOfDatesInTwoWeeksAgo)

        Log.d("TAG", "monthBreakDown: $currentWeek")
        Log.d("TAG", "monthBreakDown: $monthWeekMap")

        return monthWeekMap
    }
}

fun getEpoch(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0): Pair<Int, String> {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(year, month, day, hour, minute)
    return calendar.toInstant().epochSecond.toInt() to
            calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
}

