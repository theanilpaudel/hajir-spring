package com.zepling.hajir.utils

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.zepling.hajir.resourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.io.FileInputStream
import java.io.IOException

@Configuration
class BeanConfiguration{
    @Bean
    fun initializeFirebase(){
        try {
            println("initializing firebase bean")
            /*val resource: Resource =
                resourceLoader.getResource("classpath:firebase/hajir-10448-firebase-adminsdk-vw6rv-b6f712cf66.json")
            val serviceAccount = resource.inputStream
            val credentials = GoogleCredentials.fromStream(serviceAccount)
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options)*/


            val serviceAccount = FileInputStream("/Users/anilpaudel/SpringProjects/hajir/src/main/resources/firebase/hajir-10448-firebase-adminsdk-vw6rv-b6f712cf66.json")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}