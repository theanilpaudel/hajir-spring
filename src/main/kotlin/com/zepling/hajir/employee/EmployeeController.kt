package com.zepling.hajir.employee

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("\${API.URL}/employee")
class EmployeeController {
    @Autowired
    lateinit var employeeService: EmployeeService

    @PostMapping("/create")
    fun createEmployee(principal: Principal,@RequestBody employee: Employee):ResponseEntity<HashMap<String,Employee>>{
        val employeee = employeeService.createEmployee(principal,employee)
        val map = HashMap<String,Employee>()
        map["employee"] = employeee
        return ResponseEntity(map, HttpStatus.OK)
    }

    @GetMapping("/getAll")
    fun getAllEmployees(principal: Principal): ResponseEntity<HashMap<String,List<Employee>>> {
        val employees = employeeService.getAllEmployees(principal)
        val map = HashMap<String,List<Employee>>()
        map["employees"] = employees
        return ResponseEntity(map,HttpStatus.OK)
    }

    @PostMapping("/createSheet")
    fun createSheet(principal: Principal,employeeId:String):ResponseEntity<String>{
        employeeService.createSheet(employeeId)
        return ResponseEntity("Ok", HttpStatus.OK)
    }
}