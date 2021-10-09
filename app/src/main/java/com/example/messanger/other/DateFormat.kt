package com.example.messanger.other


import java.text.SimpleDateFormat
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

}