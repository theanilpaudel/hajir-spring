package com.zepling.hajir.boss

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.zepling.hajir.utils.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BossService {
    @Autowired
    lateinit var bossRepo: BossRepo

    fun createBoss(boss:Boss):Response<Boss>{
        with(boss){
            val request = UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber(phone)
                .setDisplayName(name)
                .setDisabled(false)

            val userRecord = FirebaseAuth.getInstance().createUser(request)
            return if (userRecord.tenantId.isNullOrBlank() || userRecord.tenantId.isNullOrEmpty()){
                id = userRecord.uid
                bossRepo.save(boss)
                Response.Success(boss)
            }else{
                Response.Error("Failed")
            }
        }

    }
}