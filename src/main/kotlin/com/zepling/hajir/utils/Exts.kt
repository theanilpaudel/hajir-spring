package com.zepling.hajir.utils

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun OffsetDateTime.convertToNepali():OffsetDateTime{
    val odt = OffsetDateTime.of(this.toLocalDateTime(), ZoneOffset.of("+05:45")).atZoneSimilarLocal(ZoneId.of(Keys.ZONE_ID)).toOffsetDateTime()


    return odt
}