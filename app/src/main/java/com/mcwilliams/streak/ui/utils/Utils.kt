package com.mcwilliams.streak.ui.utils

import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mcwilliams.streak.ui.dashboard.UnitType
import com.mcwilliams.streak.ui.utils.Food.MacAndCheese
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

sealed class Food(val foodName: String){
    class MacAndCheese() : Food("Mac and Cheese")

}


fun doSomethign(foodName: Food){
    var boolean : Boolean

    when(foodName.foodName){

    }
}

fun callIt(){
    doSomethign(MacAndCheese())
}


fun String.getDate(): LocalDate {
    val dtf = DateTimeFormatter.ISO_DATE_TIME
    val zdt: ZonedDateTime =
        ZonedDateTime.parse(this, dtf)
    val localDateTime = zdt.toLocalDateTime()
    return localDateTime.toLocalDate()
}

fun String.getTime(): LocalTime {
    val dtf = DateTimeFormatter.ISO_DATE_TIME
    val zdt: ZonedDateTime =
        ZonedDateTime.parse(this, dtf)
    val localDateTime = zdt.toLocalDateTime()
    return localDateTime.toLocalTime()
}

//Gets bar graph height based on miles
fun Int.getBarHeight() = when (this.div(1609)) {
    in 1..2 -> {
        20.dp
    }

    in 2..5 -> {
        50.dp
    }

    in 5..8 -> {
        65.dp
    }

    in 8..100 -> {
        90.dp
    }

    else -> {
        0.dp
    }
}


fun Double.getBarHeight() : Dp {
    val miles = this.div(1609)
    Log.d("TAG", "getBarHeight: ${miles.div(100)}")
    return 100.times(miles.div(100)).dp
}

fun String.getDateTime(): LocalDateTime {
    val dtf = DateTimeFormatter.ISO_DATE_TIME
    val zdt: ZonedDateTime =
        ZonedDateTime.parse(this, dtf)
    return zdt.toLocalDateTime()
}

//Calculates pace from the moving time with the distance as an arguement
fun Int.getPaceFromMovingTime(distance: Float): String {
    val secondsPerMile = this / (distance / 1609)
    return secondsPerMile.toInt().getTimeString()
}

//Returns time based on seconds passed in
fun Int.getTimeString(): String {
    return "${(this / 60)}:${(this % 60)}"
}

//Returns time based on seconds passed in
fun Int.getTimeFloat(): Float {
    return "${(this / 60)}.${((this % 60))}".toFloat()
}

fun LocalTime.get12HrTime(): String {
    val pattern = "hh:mm a"
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalDate.getDateString(): String {
    return this.month.name.fixCase() + " " + this.dayOfMonth
}

fun Float.getMiles(): Double = (this * 0.000621371192).round(2);

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun String.fixCase(): String = this.toLowerCase().capitalize()

fun getDateTimeDisplayString(date: LocalDate, time: LocalTime): String =
    "${date.dayOfWeek.name.fixCase()}, ${date.month.name.fixCase()} ${date.dayOfMonth}, ${date.year} at ${time}"

fun Int.poundsToKg(): Int = (this / 2.205).toInt()

fun caloriesBurned(met: Float, weight: Int, minutesWorkedOut: Int): Int {
    val caloriesPerMinute = (met * 3.5 * weight.poundsToKg()) / 200
    return (caloriesPerMinute * minutesWorkedOut).toInt()
}

//Returns time based on seconds passed in
fun Int.getTimeStringHoursAndMinutes(): String {
    val hours = this / 3600
    return if (hours == 0) {
        "${((this % 3600) / 60)}m"
    } else if (hours > 24) {
        val days = hours / 24
        val remainderHours = hours - (days * 24)
        "${days}d ${remainderHours}h"
    } else
        "${hours}h ${((this % 3600) / 60)}m"
}

fun Float.getElevationString(selectedUnitType: UnitType): String {
    when (selectedUnitType) {
        UnitType.Imperial -> {
            var elevation: Number = (this * 3.281).round(1)
            var elevationMeasurement = "ft"

            if (elevation.toInt() > 1500) {
                elevation = (this * 1.094).round(1)
                elevationMeasurement = "yd"
            }

            if (elevation.toInt() > 1000) {
                elevation = (this / 1609).toDouble().round(1)
                elevationMeasurement = "mi"
            }

            return "${
                elevation.toString().replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
            } $elevationMeasurement"
        }
        UnitType.Metric -> {
            return "${
                this.toDouble().round(1).toString().replace("0*$".toRegex(), "")
                    .replace("\\.$".toRegex(), "")
            } m"
        }
    }
}

fun Float.getDistanceMiles(selectedUnitType: UnitType): Double {
    return when (selectedUnitType) {
        UnitType.Imperial -> {
            this.div(1609).toDouble().round(2)
        }
        UnitType.Metric -> {
            (this / 1000).toDouble().round(2)
        }
    }
}

fun Float.getDistanceString(selectedUnitType: UnitType, isYearSummary: Boolean = false): String {
    val decimals = if (isYearSummary) 0 else 1

    return when (selectedUnitType) {
        UnitType.Imperial -> {
            "${
                this.div(1609).toDouble().round(decimals).toString().replace("0*$".toRegex(), "")
                    .replace("\\.$".toRegex(), "")
            } mi"
        }
        UnitType.Metric -> {
            var elevation: Number = (this / 1000).toDouble().round(decimals)
            "${elevation.toString().replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")} km"
        }
    }
}

fun Int.format(): String{
    return if(this in 0..9) "0$this" else this.toString()
}

fun getAveragePaceString(distance: Float, time: Int, selectedUnitType: UnitType): String {

    when(selectedUnitType){
        UnitType.Imperial -> {
            val distanceInMiles = distance.div(1609).toDouble().round(2)
            val minutes = time.div(60)

            val pace = minutes.div(distanceInMiles).round(2)

            val remainder = (pace - pace.toInt())
            val secondsPace = remainder.times(60).toInt()

            return "${pace.toInt()}:${secondsPace.format()} / mi"
        }
        UnitType.Metric -> {
            val distanceInMeters = distance.div(1000).toDouble().round(2)
            val minutes = time.div(60)

            val pace = minutes.div(distanceInMeters).round(2)

            val remainder = (pace - pace.toInt())
            val secondsPace = remainder.times(60).toInt()

            return "${pace.toInt()}:${secondsPace.format()} / km"
        }
    }
}

fun Float.getAveragePaceFromDistance(time: Int): Double {
    val distanceInMiles = this.div(1609).toDouble().round(2)
    val minutes = time.div(60)

    return minutes.div(distanceInMiles).round(2)
}