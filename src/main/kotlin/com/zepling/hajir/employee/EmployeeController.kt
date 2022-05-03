package com.zepling.hajir.employee

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("\${API.URL}/employee")
class EmployeeController {
    @Autowired
    lateinit var employeeService: EmployeeService

    @PostMapping("/create")
    fun createEmployee(principal: Principal,employee: Employee):ResponseEntity<String>{
        employeeService.createEmployee(principal,employee)
        return ResponseEntity("Success", HttpStatus.OK)
    }
}