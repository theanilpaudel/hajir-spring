package com.zepling.hajir.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset

fun OffsetDateTime.convertToNepali():OffsetDateTime{
    return OffsetDateTime.of(this.toLocalDateTime(), ZoneOffset.of("+05:45"))
}