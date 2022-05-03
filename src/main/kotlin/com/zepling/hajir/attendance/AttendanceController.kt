package com.zepling.hajir.attendance

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
    fun createAttendance(principal: Principal, employeePhone: String, remarks: String): ResponseEntity<Attendance> {
        val attendance = attendanceService.createAttendance(principal, employeePhone, remarks)
        return if (attendance == null) {
            ResponseEntity(attendance, HttpStatus.FORBIDDEN)
        } else {
            ResponseEntity(attendance, HttpStatus.OK)
        }
    }

    @GetMapping("/getLatest")
    fun getLatestAttendance(principal: Principal, employeePhone: String): ResponseEntity<Attendance> {


        return when (val attendanceResponse = attendanceService.getLatestAttendance(principal, employeePhone)) {
            is Response.Success->{

                ResponseEntity(attendanceResponse.t, HttpStatus.OK)
            }
            is Response.Error->{
                println("NOT FOUND IN CONTROLLER")
                ResponseEntity(Attendance(), HttpStatus.NOT_FOUND)
            }

        }
    }

    @GetMapping("/getAllOfAnEmployee")
    fun getAllOfAnEmployee(
        principal: Principal,
        employeeId: String
    ): ResponseEntity<HashMap<String, List<Attendance>>> {
        val attendanceResponse = attendanceService.getAllAttendanceOfAnEmployee(principal, employeeId)
        val map = HashMap<String, List<Attendance>>()
        return when (attendanceResponse) {
            is Response.Success -> {

                map["attendances"] = attendanceResponse.t
                ResponseEntity(map, HttpStatus.OK)
            }
            is Response.Error -> {
                map["attendances"] = emptyList()
                ResponseEntity(map, HttpStatus.NOT_FOUND)
            }
        }
    }

    @GetMapping("/getAllOnGoing")
    fun getAllOnGoing(principal: Principal): ResponseEntity<HashMap<String, List<Attendance>>> {
        val attendanceResponse = attendanceService.getAllOngoingAttendance(principal)
        val map = HashMap<String, List<Attendance>>()

        return when (attendanceResponse) {
            is Response.Success -> {

                map["attendances"] = attendanceResponse.t
                ResponseEntity(map, HttpStatus.OK)
            }
            is Response.Error -> {
                map["attendances"] = emptyList()
                ResponseEntity(map, HttpStatus.NOT_FOUND)
            }
        }
    }


}