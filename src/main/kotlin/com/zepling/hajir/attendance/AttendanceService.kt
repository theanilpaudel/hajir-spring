package com.zepling.hajir.attendance

import com.zepling.hajir.boss.BossRepo
import com.zepling.hajir.employee.Employee
import com.zepling.hajir.employee.EmployeeRepo
import com.zepling.hajir.utils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.Principal
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Service
class AttendanceService {
    @Autowired
    lateinit var attendanceRepo: AttendanceRepo

    @Autowired
    lateinit var employeeRepo: EmployeeRepo

    @Autowired
    lateinit var bossRepo: BossRepo

    @Autowired
    lateinit var googleSheetsUtil: GoogleSheetsUtil

    fun createAttendance(principal: Principal, employeePhone: String, remarks: String): Attendance? {
        val boss = bossRepo.findById(principal.name).get()
        val employeeOpt = employeeRepo.findByBossIdAndPhone(boss.id.toString(),employeePhone)
        if(!employeeOpt.isPresent){
            return null
        }
        val employee = employeeOpt.get()


        val lastAttendanceOpt = employee.id?.let { attendanceRepo.findFirstByEmployee_Id_OrderByCheckInDesc(it) }

        var attendance: Attendance?

        if (lastAttendanceOpt?.isPresent == true) {
            //check checkOut to see if it's a new checkIn
            with(lastAttendanceOpt.get()) {
                attendance = if (checkOut == null) {
                    //Employee hasn't checked out :: CHECKOUT
                    checkOut(this, remarks)
                } else {
                    //Employee has checked out :: CHECKIN
                    checkIn(employee, remarks)
                }
            }

            println(lastAttendanceOpt.get().checkIn)
        } else {
            //CHECKIN
            attendance = checkIn(employee, remarks)
        }

        removeOlderAttendance()
//        attendance?.let { writeMonthInSpreadSheet(principal, it) }
        attendance?.let { writeAttendanceToSpreadSheet(it) }
        return attendance
    }

    fun checkIn(employee: Employee, remarks: String): Attendance = Attendance().apply {
        this.checkIn = OffsetDateTime.now(ZoneOffset.of(Keys.ZONE_OFFSET))
        this.remarksCheckIn = remarks
        this.employee = employee
    }.also {
        attendanceRepo.save(it)
    }

    fun checkOut(attendance: Attendance, remarks: String): Attendance = attendance.apply {
        this.checkIn = attendance.checkIn
        this.checkOut = OffsetDateTime.now(ZoneId.of(Keys.ZONE_OFFSET))
        this.remarksCheckOut = remarks
    }.also {
        attendanceRepo.save(it)
    }

    fun removeOlderAttendance(){
        val now = OffsetDateTime.now(ZoneId.of("UTC")).plusDays(10)
        val allAttendancesOpt = attendanceRepo.findAllByCheckOutIsAfter(now)
        if(allAttendancesOpt.isPresent){
            val allAttendance = allAttendancesOpt.get()
//            println("removing all attendaces ${allAttendance[0].checkOut}")
            val ids = allAttendance.map {
//                it.checkOut?.isAfter(now)
                it.id
            }
            ids.forEach {
                println("OLDER IDS $it")
            }
            attendanceRepo.deleteAllById(ids)
        }
    }


    fun getLatestAttendance(principal: Principal, employeePhone: String): Response<Attendance> {
        val boss = bossRepo.findById(principal.name).get()
        val employeeOpt = employeeRepo.findByBossIdAndPhone(boss.id.toString(),employeePhone)
        if(!employeeOpt.isPresent){
            println("NOT FOUND IN SERVICE")
            return Response.Error("Not found")
        }
        val employee = employeeOpt.get()
        val lastAttendanceOpt = employee.id?.let { attendanceRepo.findFirstByEmployee_Id_OrderByCheckInDesc(it) }
        return if (lastAttendanceOpt?.isPresent == true) {
            lastAttendanceOpt.get().apply {
                this.checkIn?.convertToNepali()
                this.checkOut?.convertToNepali()
            }
            Response.Success(lastAttendanceOpt.get())
        } else {
            val attendance = Attendance().apply {
                this.employee = employee
            }
            Response.Success(attendance)
        }


    }

    fun getAllAttendanceOfAnEmployee(principal: Principal, employeeId: String): Response<List<Attendance>> {
        val boss = bossRepo.findById(principal.name).get()
        val employeeOpt = employeeRepo.findByBossIdAndId(boss.id.toString(),employeeId)
        if(!employeeOpt.isPresent){
            println("NOT FOUND IN SERVICE")
            return Response.Error("Not found")
        }
        val employee = employeeOpt.get()
        val listOpt = employee.id?.let { attendanceRepo.findAllByEmployee_Id_OrderByCheckInDesc(it) }
        return if (listOpt?.isPresent == true) {

            Response.Success(listOpt.get())
        } else {
            Response.Error(Keys.EMPTY)
        }
    }

    fun getAllOngoingAttendance(principal: Principal): Response<List<Attendance>> {
        val boss = bossRepo.findById(principal.name).get()
        val listOpt = boss.id?.let { attendanceRepo.findAllByEmployee_Boss_IdOrderByCheckInDesc(it) }
        return if (listOpt?.isPresent == true) {
            val list = listOpt.get().filter {
                (it.checkIn != null && it.checkOut == null)
            }
            Response.Success(list)
        } else {
            Response.Error(Keys.EMPTY)
        }
    }

    fun writeMonthInSpreadSheet(principal: Principal, attendance: Attendance) {
        val boss = bossRepo.findById(principal.name).get()
        //we need to write month at the first time and at the end of a month

        if (attendanceRepo.findAllByEmployee_Boss_IdOrderByCheckInDesc(boss.id.toString()).isPresent) {
            //not first time
            val latestAttendance =
                attendanceRepo.findFirstByEmployee_Id_OrderByCheckInDesc(attendance.employee.id.toString()).get()
            if (latestAttendance.checkOut != null) {
                //calculate from checkout
                val offSetOld = latestAttendance.checkOut.toString().getMonthAndYearOffset()
                val offSetNow = OffsetDateTime.now(ZoneId.of(Keys.ZONE_ID))
                if (offSetNow.month != offSetOld.month) {
                    googleSheetsUtil.writeMonthToSpreadSheet(attendance, attendance.checkOut.toString())
                }
            } else {
                //calculate from checkin
                val offSetOld = latestAttendance.checkIn.toString().getMonthAndYearOffset()
                val offSetNow = OffsetDateTime.now(ZoneId.of(Keys.ZONE_ID))
                if (offSetNow.month != offSetOld.month) {
                    googleSheetsUtil.writeMonthToSpreadSheet(attendance, attendance.checkIn.toString())
                }
            }
        } else {
            //first time
            googleSheetsUtil.writeMonthToSpreadSheet(attendance,attendance.checkIn.toString())
        }

        attendance.employee.apply {
            range?.plus(1)
        }.also {
            employeeRepo.save(it)
        }
    }

    fun writeAttendanceToSpreadSheet(attendance: Attendance){
        val isAdded = googleSheetsUtil.writeAttendanceToSpreadSheet(attendance)
        if(isAdded == null){

        }else if(isAdded){
            attendance.employee.range = attendance.employee.range?.plus(1)
            employeeRepo.save(attendance.employee)
        }

    }
}