package com.zepling.hajir.employee

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zepling.hajir.boss.Boss
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "employee")
open class Employee {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    open var id: String? = null

    open var name:String?=null
    open var phone:String?=null
    open var address:String?=null
    open var bloodGrp:String?=null
    open var range:Long?=2


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="boss_id", nullable = false)
    open lateinit var boss:Boss
}