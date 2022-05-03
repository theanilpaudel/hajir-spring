package com.zepling.hajir.attendance

import com.zepling.hajir.employee.Employee
import com.zepling.hajir.employee.EmployeeRepo
import com.zepling.hajir.utils.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.Principal
import java.time.OffsetDateTime
import java.time.ZoneId

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
            //check checkOut to see if its a new checkIn
            lastAttendanceOpt.get().also {
                if (it.checkOut == null) {
                    //Employee hasn't checked out :: CHECKOUT
                    attendance = checkOut(it,remarks)
                } else {
                    //Employee has checked out :: CHECKIN
                    attendance = checkIn(employee,remarks)
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
        checkIn = OffsetDateTime.now(ZoneId.of("Asia/Kathmandu"))
        this.remarks = remarks
        this.employee = employee
    }.also {
        attendanceRepo.save(it)
    }

    fun checkOut(attendance: Attendance, remarks: String):Attendance = attendance.apply {
        checkOut = OffsetDateTime.now(ZoneId.of("Asia/Kathmandu"))
        this.remarks = remarks
    }.also {
        attendanceRepo.save(it)
    }
}