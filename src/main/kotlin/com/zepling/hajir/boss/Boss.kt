package com.zepling.hajir.boss

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "boss")
open class Boss {
    @Id
    @Column(name = "id", nullable = false)
    open var id: String? = null

    open var name:String?=null
    open var password:String?=null
    open var email:String?=null
    open var phone: String?=null
    open var enabled:Boolean?=null
    @JsonIgnore
    open var spreadSheetId:String?=null
}