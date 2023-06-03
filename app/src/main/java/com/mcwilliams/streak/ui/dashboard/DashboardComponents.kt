package com.mcwilliams.streak.ui.dashboard

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StreakWidgetCard(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
            .clickable(
                enabled = onClick != null,
                onClickLabel = null,
                role = null,
                onClick = { onClick?.let { it.invoke() } }),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        content()
    }
}


@Composable
fun PercentDelta(now: Number, then: Number, monthColumnWidth: Dp, type: StatType) {
    var percent = 0.0

    when (type) {
        StatType.Distance -> {
            Log.d(
                "TAG",
                "PercentDelta: ${(now.toInt().div(1609)).toDouble()}  ${
                    (then.toInt().div(1609)).toDouble()
                }"
            )
            percent = (now.toDouble().div(1609)) / (then.toDouble().div(1609))
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

        StatType.Pace -> {
            percent = now.toDouble() / then.toDouble()
        }
    }

    var surfaceColor: Color
    var percentString: String
    var textColor: Color

    if (type == StatType.Pace) {
        if (then.toDouble() < now.toDouble()) {
            percent = (1.0 - percent) * 100
            percentString = "${percent.toInt()}%"
            surfaceColor = Color(0xFF990000)
            textColor = Color.White
        } else {
            percent = (1.0 - percent) * 100
            percentString = "+${Math.abs(percent.toInt())}%"
            surfaceColor = Color(0xFF008000)
            textColor = Color.White
        }
    } else if (then.toDouble() == 0.0) {
        percent = (1.0 - percent) * 100
        percentString = "---"
        surfaceColor = Color(0xFF990000)
        textColor = Color.White
    } else if (then.toDouble() > now.toDouble()) {
        percent = (1.0 - percent) * 100
        percentString = "-${percent.toInt()}%"
        surfaceColor = Color(0xFF990000)
        textColor = Color.White
    } else {
        percent = (1.0 - percent) * 100
        percentString = "+${Math.abs(percent.toInt())}%"
        surfaceColor = Color(0xFF008000)
        textColor = Color.White
    }

    Surface(
        color = surfaceColor,
        modifier = Modifier
            .width(monthColumnWidth)
            .padding(horizontal = 4.dp),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = percentString,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Composable
fun DashboardStat(
    @DrawableRes image: Int,
    stat: String? = null,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = "",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f)
        )
        stat?.let {
            Text(
                text = it,
                modifier = Modifier
                    .padding(start = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun MonthTextStat(monthStat: String, monthColumnWidth: Dp, isLoading: Boolean = false) {
    Text(
        text = monthStat,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .width(monthColumnWidth),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
    )
}