package com.zepling.hajir.utils

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.common.collect.Lists
import com.zepling.hajir.attendance.Attendance
import com.zepling.hajir.boss.Boss
import com.zepling.hajir.employee.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.io.*
import java.util.*


object GoogleSheetsUtil {
    private val APPLICATION_NAME = "Cloud9"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val TOKENS_DIRECTORY_PATH = "tokens"
    @Autowired
    lateinit var resourceLoader: ResourceLoader
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(
        SheetsScopes.SPREADSHEETS,
        SheetsScopes.DRIVE,
        SheetsScopes.DRIVE_FILE,
    )
    private val CREDENTIALS_FILE_PATH =
        "/client_secret_569046147684-mi7cilm08635aum55kdf60isj1lotlb1.apps.googleusercontent.com.json"

    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val spreadsheetId = "***REMOVED***"
    val range = "Class Data!A2:E"

    private val service: Sheets by lazy {

        Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, HttpCredentialsAdapter(getCredentials()))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    /*@Throws(IOException::class)
    private fun getCredentials(httpTransport: NetHttpTransport): Credential? {
        // Load client secrets.
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, GoogleSheetsUtil::class.java.getResourceAsStream(
            CREDENTIALS_FILE_PATH
        )?.let { InputStreamReader(it) })

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setApprovalPrompt("force")
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }*/

    private fun getCredentials(): GoogleCredentials {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        val resource: Resource =
            resourceLoader.getResource("classpath:hajir-10448-714a576aaeab.json")
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(resource.inputStream)
//        val credentials: GoogleCredentials = GoogleCredentials.fromStream(FileInputStream("/Users/anilpaudel/SpringProjects/hajir/src/main/resources/hajir-10448-714a576aaeab.json"))
            .createScoped(SCOPES)

        return credentials
    }

    fun createSpreadSheet(boss: Boss): String {
        var spreadsheet = Spreadsheet()
            .setProperties(
                SpreadsheetProperties()
                    .setTitle("${boss.name} Attendance")
            )
        spreadsheet = service.spreadsheets().create(spreadsheet)
            .setFields("spreadsheetId")
            .execute()

        println("Spreadsheet ID: " + spreadsheet.spreadsheetId)

        return spreadsheet.spreadsheetId
    }

    fun createSheet(employee: Employee) {
        val request = Request()
        val addSheetRequest = AddSheetRequest()

        val sheetProperties = SheetProperties()
        sheetProperties.title = employee.name

        addSheetRequest.properties = sheetProperties

        request.addSheet = addSheetRequest

        val requests = BatchUpdateSpreadsheetRequest().setRequests(listOf(request))

        service.spreadsheets()
            .batchUpdate(employee.boss.spreadSheetId, requests)
            .execute()

        writeHeaderToSpreadSheet(employee)
    }

    private fun writeHeaderToSpreadSheet(employee: Employee) {
        val range = "${employee.name}!A1"
        val values: List<List<String>> = listOf(
            listOf(
                "ID",
                "Check In",
                "Check In Remarks",
                "Check Out",
                "Check Out Remarks",
                "Total Hours"
            ) // Cell values
        )// Rows
        val data: MutableList<ValueRange> = ArrayList()
        data.add(
            ValueRange()
                .setRange(range)
                .setValues(values)
        )
        val body = ValueRange()
            .setValues(values)

        val result = service.spreadsheets()
            .values()
            .append(employee.boss.spreadSheetId, range, body)
            .setValueInputOption("raw")
            .execute()


    }

    fun writeMonthToSpreadSheet(attendance: Attendance, date: String) {
        val range = "${attendance.employee.name}!A2"
        val values: List<List<String>> = listOf(
            listOf(date.getMonthAndYear()) // Cell values
        )// Rows
        val data: MutableList<ValueRange> = ArrayList()
        data.add(
            ValueRange()
                .setRange(range)
                .setValues(values)
        )
        val body = ValueRange()
            .setValues(values)

        val result = service.spreadsheets()
            .values()
            .append(attendance.employee.boss.spreadSheetId, range, body)
            .setValueInputOption("raw")
            .execute()


    }

    fun writeAttendanceToSpreadSheet(attendance: Attendance):Boolean? {
        try {
            val spreadSheetId = attendance.employee.boss.spreadSheetId
            val range = "${attendance.employee.name}!A3"
            println("CHECK IN SpreadSheet -> ${attendance.checkIn.toString()}")
            println("CHECK IN beautified SpreadSheet -> ${attendance.checkIn.toString().beautifyDateWithTimeZone()}")
            val values: List<List<String>> = listOf(
                listOf(
                    attendance.id.toString(),
                    attendance.checkIn.toString().beautifyDateWithTimeZone().toString(),
                    if (attendance.remarksCheckIn.isNullOrBlank() || attendance.remarksCheckIn.isNullOrEmpty()) "N/A" else attendance.remarksCheckIn.toString(),
                    attendance.checkOut.toString().beautifyDateWithTimeZone().toString(),
                    if (attendance.remarksCheckOut.isNullOrBlank() || attendance.remarksCheckOut.isNullOrEmpty()) "N/A" else attendance.remarksCheckOut.toString(),
                    attendance.hours
                ) // Cell values
            )// Rows

            val body = ValueRange()
                .setValues(values)

            if (attendance.checkOut == null) {
                //add new row


                val result = service.spreadsheets()
                    .values()
                    .append(spreadSheetId, range, body)
                    .setInsertDataOption("INSERT_ROWS")
                    .setValueInputOption("raw")
                    .execute()

                return true
            } else {
                //update on same row


                val newRange = "${attendance.employee.name}!A${attendance.employee.range}"

                val valueRange = ValueRange()
                    .setValues(values)


                val result = service.spreadsheets()
                    .values()
                    .update(spreadSheetId, newRange, valueRange)
                    .setValueInputOption("raw")
                    .execute()


                return false
            }

        }catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    fun printValues() {
        // Build a new authorized API client service.


        val response: ValueRange = service.spreadsheets().values()[spreadsheetId, range]
            .execute()
        val values: List<List<Any>> = response.getValues()
        if (values.isEmpty()) {
            println("No data found.")
        } else {
            println("Name, Major")
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row[0], row[4])
            }
        }
    }
}

