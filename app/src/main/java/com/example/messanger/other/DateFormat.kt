package com.example.messanger.other


import android.util.Log
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

object DateFormat {
private const val dayMillis=86400000

    fun format(timestamp: Long): String =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )
            .format(Date(timestamp))

    fun day(timestamp: Long): Long {

        return abs(timestamp / dayMillis)
    }

    fun formatDay(timestamp: Long):String= SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.getDefault()
    )
        .format(Date(timestamp))

        // конвертировать дату в лонг

    fun toDatePattern(timestamp: Long,pattern:String="dd/MM/yyyy", offset:Long=0L): String {
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).withZone(ZoneId.systemDefault())
        val instant: Instant = Instant.ofEpochSecond(timestamp+offset)

        return dateTimeFormatter.format(instant)
    }

    fun toTimeStamp(date:String): Long {
        val dateFormat= SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.parse(date).time
    }

    fun currentDate(): Long {
        val calendar= Calendar.getInstance(TimeZone.getTimeZone("GMT"),
            Locale.getDefault());
        val localTime=calendar.time
        val date=SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        date.format(localTime)
        date.timeZone= TimeZone.getDefault()
        return date.calendar.timeInMillis
    }
}