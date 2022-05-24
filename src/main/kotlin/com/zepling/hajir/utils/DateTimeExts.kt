package com.zepling.hajir.utils

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by Anil
 */
fun String.getDuration():String{
    val odt = OffsetDateTime.parse(this)
    val odtNow = OffsetDateTime.now(ZoneId.of(Keys.ZONE_ID))

    val duration = Duration.between(odt,odtNow)

    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()
    val seconds = duration.minusHours(hours).minusMinutes(minutes).seconds

    return "$hours:$minutes:$seconds"
}

fun String.getDurationInMilis():Long{
    val odt = OffsetDateTime.parse(this)
    val odtNow = OffsetDateTime.now(ZoneId.of(Keys.ZONE_ID))


    val duration = Duration.between(odt,odtNow)
    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()
    val seconds = duration.minusHours(hours).minusMinutes(minutes).seconds
    val value = hours * 60000 * 60 + minutes * 60000 + seconds * 1000
    return value
}

fun String.getDurationInMinutes(): Long {
    val odt = OffsetDateTime.parse(this)
    val odtNow = OffsetDateTime.now(ZoneId.of(Keys.ZONE_ID))


    val duration = Duration.between(odt, odtNow)
    return duration.toMinutes()
}

fun String.beautifyDate():String?{
    try {
        //2022-05-10T20:32:39.381+05:45
        val dateParse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
        val dateFormat = SimpleDateFormat("MMM d, yyyy, hh:mm a", Locale.ENGLISH)

        val d = dateParse.parse(this)
        return dateFormat.format(d)
    }catch (e:Exception){
        return null
    }
}

fun String.beautifyDateWithTimeZone():String?{
    return try {
        //2022-05-10T20:32:39.381+05:45
        val dateParse = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
        val odt = ZonedDateTime.parse(this, dateParse).withZoneSameInstant(ZoneId.of(Keys.ZONE_ID))

        val newOdt = OffsetDateTime.of(odt.toLocalDateTime(), ZoneOffset.of("+05:45"))
        val dateTimeFormat = DateTimeFormatter.ofPattern("MMM d, yyyy, HH:mm:ss", Locale.ENGLISH)

        val d = OffsetDateTime.of(newOdt.toLocalDateTime(),ZoneOffset.of("+05:45")).atZoneSameInstant(ZoneId.of(Keys.ZONE_ID)).format(dateTimeFormat)

        d
    }catch (e:Exception){
        e.printStackTrace()
        null
    }
}

fun String.getMonthAndYear():String{
    //2022-05-10T20:32:39.381+05:45
    val dateParse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
    val dateFormat = SimpleDateFormat("MMM, yyyy", Locale.ENGLISH)

    val d = dateParse.parse(this)
    return dateFormat.format(d)
}

fun String.getMonthAndYearOffset():OffsetDateTime{
    //2022-05-10T20:32:39.381+05:45
    val dateParse = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH)
    val odt = OffsetDateTime.parse(this,dateParse)

    val newOdt = OffsetDateTime.of(odt.toLocalDateTime(), ZoneOffset.of("+05:45"))
    val dateTimeFormat = DateTimeFormatter.ofPattern("MMM, yyyy", Locale.ENGLISH)


    return OffsetDateTime.of(newOdt.toLocalDateTime(),ZoneOffset.of("+05:45"))
}