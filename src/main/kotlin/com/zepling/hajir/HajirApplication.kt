package com.zepling.hajir

import it.ozimov.springboot.mail.configuration.EnableEmailTools
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@SpringBootApplication
@EnableJpaAuditing
@EnableEmailTools
class HajirApplication



fun main(args: Array<String>) {
    runApplication<HajirApplication>(*args)
}






