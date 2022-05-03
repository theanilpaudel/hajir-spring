package com.zepling.hajir.utils

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import com.zepling.hajir.attendance.Attendance
import com.zepling.hajir.boss.Boss
import com.zepling.hajir.employee.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GoogleSheetsUtil {
    @Autowired
    lateinit var service: Sheets

    @Value ("\${spreadsheet.id}")
    lateinit var spreadsheetId:String

    val range = "Class Data!A2:E"


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

    fun writeAttendanceToSpreadSheet(attendance: Attendance): Boolean? {
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

        } catch (e: Exception) {
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

