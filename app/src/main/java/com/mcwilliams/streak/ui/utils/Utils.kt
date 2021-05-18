package com.mcwilliams.streak.ui.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

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
    return "${(this / 3600)}h ${(this % 60)}m"
}

fun Float.getElevationString(): String {
    var elevation = (this * 3.281).toInt()
    var elevationMeasurement = "ft"

    if (elevation > 1500) {
        elevation = (this * 1.094).toInt()
        elevationMeasurement = "yd"
    }

    return "$elevation $elevationMeasurement"
}

fun Float.getDistanceString() = "${this.div(1609).roundToInt()} mi"