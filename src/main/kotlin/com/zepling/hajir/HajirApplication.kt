package com.zepling.hajir

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import it.ozimov.springboot.mail.configuration.EnableEmailTools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.io.FileInputStream
import java.io.IOException


@SpringBootApplication
@EnableJpaAuditing
@EnableEmailTools
class HajirApplication



fun main(args: Array<String>) {
    runApplication<HajirApplication>(*args)
}






