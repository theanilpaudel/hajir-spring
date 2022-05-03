package com.zepling.hajir.boss

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
@RequestMapping("\${API.URL}/boss")
class BossController {
    @Autowired
    lateinit var bossService: BossService

    @GetMapping("/get")
    fun getBoss(principal: Principal): String? {
        return principal.name
    }

    @PostMapping("/create")
    fun createBoss(boss: Boss): ResponseEntity<String> {
        return when (bossService.createBoss(boss)) {
            is Response.Success -> {
                ResponseEntity("OK", HttpStatus.OK)
            }
            is Response.Error -> {
                ResponseEntity("Error", HttpStatus.NOT_FOUND)
            }
        }


    }

    /*@PostMapping("/createSpreadSheet")
    fun createSpreadSheet(principal: Principal): ResponseEntity<String> {

        return when (bossService.createSpreadSheet(principal)) {
            is Response.Success -> {
                ResponseEntity("OK", HttpStatus.OK)
            }
            is Response.Error -> {
                ResponseEntity("Error", HttpStatus.NOT_FOUND)
            }
        }


    }*/
}