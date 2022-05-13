package com.zepling.hajir.attendance

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AttendanceRepo:JpaRepository<Attendance,String> {
    fun findFirstByEmployee_Id_OrderByCheckInDesc(employeeId:String):Optional<Attendance>
    fun findAllByEmployee_Id_OrderByCheckInDesc(employeeId:String):Optional<List<Attendance>>
    fun findAllByEmployee_Boss_IdOrderByCheckInDesc(bossId:String):Optional<List<Attendance>>
}