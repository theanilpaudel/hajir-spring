package com.zepling.hajir.attendance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled


class AttendanceCron {
    @Autowired
    lateinit var attendanceService: AttendanceService

    //every day from Sun to Thu at 22:15 only once
    @Scheduled(cron = "0 17 35 * * *", zone = "Asia/Kathmandu")
    private fun cronRemoval() {
        attendanceService.removeOlderAttendance()
    }
}