package com.mcwilliams.streak.ui.dashboard

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("SimpleDateFormat")
@Composable
fun CalendarView(
    startDayOffSet: Int,
    endDayCount: Int,
    monthWeekNumber: Int,
    priorMonthLength: Int,
    weekCount: Int,
    width: Dp,
    daysActivitiesLogged: MutableList<Int>
) {
    val dateModifier = Modifier.width(width = width / 7)
    Row(modifier = Modifier.fillMaxWidth()) {

        val listOfDatesInWeek: MutableList<Int> = mutableListOf()

        if (monthWeekNumber == 0) {
            for (i in 0 until startDayOffSet) {
                val priorDay = (priorMonthLength - (startDayOffSet - i - 1))
                Log.d("TAG", "CalendarView: $priorDay")

                listOfDatesInWeek.add(priorDay)

                Text(
                    " ",
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = dateModifier
                )
            }
        }


        val endDay = when (monthWeekNumber) {
            0 -> 7 - startDayOffSet
            weekCount -> endDayCount
            else -> 7
        }

        for (i in 1..endDay) {
            val day =
                if (monthWeekNumber == 0) i else (i + (7 * monthWeekNumber) - startDayOffSet)

            val dayColor =
                when {
                    daysActivitiesLogged.contains(day) -> Color(0xFFFFA500)
                    day < today -> MaterialTheme.colors.onSurface
                    else -> Color.LightGray.copy(alpha = .8f)
                }

            Row() {
                Surface(
                    color = Color.Transparent,
                    shape = CircleShape,
                    border = if (day == today) BorderStroke(
                        1.dp,
                        color = MaterialTheme.colors.onSurface
                    ) else null
                ) {
                    Text(
                        "$day",
                        textAlign = TextAlign.Center,
                        modifier = dateModifier.padding(2.dp),
                        fontWeight = FontWeight.Medium,
                        color = dayColor,
                    )
                }
            }

            listOfDatesInWeek.add(day)
        }

        if (listOfDatesInWeek.contains(today)) {
            currentWeek = listOfDatesInWeek
        }

        monthWeekMap.put(monthWeekNumber, listOfDatesInWeek)
        Log.d("TAG", "CalendarView: $monthWeekMap")
    }
}

@Composable
fun PercentDelta(now: Int, then: Int, monthColumnWidth: Dp, type: StatType) {
    var percent: Double

    when (type) {
        StatType.Distance -> {
            percent = (now.div(1609)).toDouble() / (then.div(1609)).toDouble()
        }
        StatType.Time -> {
            percent = now.toDouble() / then.toDouble()
        }
        StatType.Elevation -> {
            percent = now.toDouble() / then.toDouble()
        }
        StatType.Count -> {
            percent = now.toDouble() / then.toDouble()
        }
    }

    var surfaceColor: Color
    var percentString: String
    if (then > now) {
        percent = (1.0 - percent) * 100
        percentString = "- ${percent.toInt()}%"
        surfaceColor = Color(0xFF990000)
    } else {
        percent = (1.0 - percent) * 100
        percentString = "${Math.abs(percent.toInt())}%"
        surfaceColor = Color(0xFF008000)
    }

    Surface(
        color = surfaceColor,
        modifier = Modifier
            .width(monthColumnWidth)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = percentString,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

}