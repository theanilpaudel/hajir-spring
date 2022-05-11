package com.zepling.hajir.attendance

import com.zepling.hajir.employee.Employee
import com.zepling.hajir.employee.EmployeeRepo
import com.zepling.hajir.utils.Response
import com.zepling.hajir.utils.convertToNepali
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.Principal
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class AttendanceService {
    @Autowired
    lateinit var attendanceRepo: AttendanceRepo

    @Autowired
    lateinit var employeeRepo: EmployeeRepo

    fun createAttendance(principal: Principal, employeeId: String, remarks: String) :Attendance?{
        val employee = employeeRepo.findById(employeeId).get()
        val lastAttendanceOpt = employee.id?.let { attendanceRepo.findFirstByEmployee_Id_OrderByCheckInDesc(it) }

        var attendance: Attendance?

        if (lastAttendanceOpt?.isPresent == true) {
            //check checkOut to see if it's a new checkIn
            with(lastAttendanceOpt.get()){
                attendance = if (checkOut == null) {
                    //Employee hasn't checked out :: CHECKOUT
                    checkOut(this,remarks)
                } else {
                    //Employee has checked out :: CHECKIN
                    checkIn(employee,remarks)
                }
            }

            println(lastAttendanceOpt.get().checkIn)
        } else {
            //CHECKIN
            attendance = checkIn(employee,remarks)
        }
        return attendance
    }

    fun checkIn(employee: Employee,remarks: String):Attendance = Attendance().apply {
        this.checkIn = OffsetDateTime.now(ZoneId.of("Asia/Kathmandu"))
        this.remarks = remarks
        this.employee = employee
    }.also {
        attendanceRepo.save(it)
    }

    fun checkOut(attendance: Attendance, remarks: String):Attendance = attendance.apply {
        this.checkIn = attendance.checkIn?.convertToNepali()
        this.checkOut = OffsetDateTime.now(ZoneId.of("Asia/Kathmandu"))
        this.remarks = remarks
    }.also {
        attendanceRepo.save(it)
    }


    fun getLatestAttendance(principal: Principal,employeeId: String):Attendance?{
        val employee = employeeRepo.findById(employeeId).get()
        val lastAttendanceOpt = employee.id?.let { attendanceRepo.findFirstByEmployee_Id_OrderByCheckInDesc(it) }
        lastAttendanceOpt?.get().apply {
            this?.checkIn?.convertToNepali()
            this?.checkOut?.convertToNepali()
        }
        return lastAttendanceOpt?.get()
    }
}