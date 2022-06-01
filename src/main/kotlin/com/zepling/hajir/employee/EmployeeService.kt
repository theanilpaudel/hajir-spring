package com.zepling.hajir.employee

import com.zepling.hajir.boss.BossRepo
import com.zepling.hajir.utils.GoogleSheetsUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class EmployeeService {
    @Autowired
    lateinit var employeeRepo: EmployeeRepo
    @Autowired
    lateinit var employerRepo: BossRepo
    @Autowired
    lateinit var googleSheetsUtil: GoogleSheetsUtil
    fun createEmployee(principal: Principal, employee: Employee):Employee{
        println("PRINCIPAL ${principal.name}")
        val boss = employerRepo.findById(principal.name).get()
        employee.boss = boss
        val emp  = employeeRepo.save(employee)
        createSheet(emp.id.toString())
        return employee
    }

    fun getAllEmployees(principal: Principal): List<Employee> {
        val boss = employerRepo.findById(principal.name).get()
        return employeeRepo.findAllByBossId(boss.id.toString()).get()
    }

    fun createSheet(employeeId:String){
        val employee = employeeRepo.findById(employeeId).get()
        googleSheetsUtil.createSheet(employee)
    }
}