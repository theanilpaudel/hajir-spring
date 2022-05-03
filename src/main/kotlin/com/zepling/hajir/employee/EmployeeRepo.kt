package com.zepling.hajir.employee

import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepo : JpaRepository<Employee,String> {
}