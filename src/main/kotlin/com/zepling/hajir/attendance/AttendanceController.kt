package com.zepling.hajir.attendance

import com.zepling.hajir.employee.Employee
import com.zepling.hajir.utils.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("\${API.URL}/attendance")
class AttendanceController {
    @Autowired
    lateinit var attendanceService: AttendanceService

    @PostMapping("/create")
    fun createAttendance(principal: Principal,employeeId: String,remarks:String):ResponseEntity<Attendance>{
        val attendance = attendanceService.createAttendance(principal,employeeId, remarks)
        return if(attendance == null){
            ResponseEntity(attendance, HttpStatus.FORBIDDEN)
        }else{
            ResponseEntity(attendance,HttpStatus.OK)
        }
    }

    @GetMapping("/getLatest")
    fun getLatestAttendance(principal: Principal,employeeId: String):ResponseEntity<Attendance>{
        val attendance = attendanceService.getLatestAttendance(principal,employeeId)
        return if(attendance == null){
            ResponseEntity(attendance, HttpStatus.FORBIDDEN)
        }else{
            ResponseEntity(attendance,HttpStatus.OK)
        }
    }
}