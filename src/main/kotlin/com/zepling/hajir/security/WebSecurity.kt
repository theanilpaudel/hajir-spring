package com.zepling.hajir.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class WebSecurity : WebSecurityConfigurerAdapter() {


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity?){
        http?.authorizeRequests()
            ?.anyRequest()
            ?.authenticated()

        http?.oauth2ResourceServer()
            ?.jwt()
    }
}