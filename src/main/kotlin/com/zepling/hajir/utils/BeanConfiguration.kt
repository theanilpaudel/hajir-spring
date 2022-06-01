package com.zepling.hajir.utils

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.io.FileInputStream
import java.io.IOException
import javax.annotation.PostConstruct

@Configuration
class BeanConfiguration{
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private val SCOPES = listOf(
        SheetsScopes.SPREADSHEETS,
        SheetsScopes.DRIVE,
        SheetsScopes.DRIVE_FILE,
    )
    private val APPLICATION_NAME = "Cloud9"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    @Bean
    fun initializeFirebase(){
        try {
            println("initializing firebase bean")
            val resource: Resource =
                resourceLoader.getResource("classpath:firebase/hajir-10448-firebase-adminsdk-vw6rv-b6f712cf66.json")
            val serviceAccount = resource.inputStream
            val credentials = GoogleCredentials.fromStream(serviceAccount)
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options)


            /*val serviceAccount = FileInputStream("/Users/anilpaudel/SpringProjects/hajir/src/main/resources/firebase/hajir-10448-firebase-adminsdk-vw6rv-b6f712cf66.json")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)*/


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Bean
    fun initializeSheets() :Sheets{
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        println("initializing google credentials bean")
        val resource: Resource =
            resourceLoader.getResource("classpath:hajir-10448-714a576aaeab.json")
        val googleCredentials= GoogleCredentials.fromStream(resource.inputStream)
            .createScoped(SCOPES)
        //GoogleCredentials.fromStream(FileInputStream("/Users/anilpaudel/SpringProjects/hajir/src/main/resources/hajir-10448-714a576aaeab.json"))
        return Sheets.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY, HttpCredentialsAdapter(googleCredentials)
        )
            .setApplicationName(APPLICATION_NAME)
            .build()
    }
}