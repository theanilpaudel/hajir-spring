package com.zepling.hajir.attendance

import com.zepling.hajir.boss.Boss
import com.zepling.hajir.employee.Employee
import org.hibernate.annotations.GenericGenerator
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

    open var checkIn: OffsetDateTime? = null
    open var checkOut: OffsetDateTime? = null
    open var remarks: String? = null
    open val hours: Int
        get() {
            return if (checkIn != null && checkOut != null) {
                (checkOut!!.second.minus(checkIn!!.second))/(60*60)
            } else {
                0
            }

        }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    open lateinit var employee: Employee
}