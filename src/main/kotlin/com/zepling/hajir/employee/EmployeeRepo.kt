package com.zepling.hajir.employee

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface EmployeeRepo : JpaRepository<Employee,String> {
    fun findAllByBossId(bossId:String):Optional<List<Employee>>
}