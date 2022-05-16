package com.zepling.hajir.attendance

import com.zepling.hajir.employee.Employee
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.time.Duration
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "attendance")
open class Attendance {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    open var id: String? = null

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    open var checkIn: OffsetDateTime? = null
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    open var checkOut: OffsetDateTime? = null
    open var remarksCheckIn: String? = null
    open var remarksCheckOut: String? = null
   
    open val hours: String
        get() {
            return if (checkIn != null && checkOut != null) {

                String.format("%.3f",(Duration.between(checkIn,checkOut).seconds)/(60*60).toDouble())
            } else {
                "0"
            }

        }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    open lateinit var employee: Employee
}