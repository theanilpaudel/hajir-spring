package com.zepling.hajir.boss

import org.springframework.data.jpa.repository.JpaRepository

interface BossRepo : JpaRepository<Boss,String> {
}