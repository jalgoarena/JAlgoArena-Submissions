package com.jalgoarena

import com.jalgoarena.web.HttpUsersClient
import com.jalgoarena.web.UsersClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@Configuration
open class AppConfiguration {

    @Value("\${jalgoarena.api.url}")
    private lateinit var jalgoarenaApiUrl: String

    @Bean
    open fun usersClient(): UsersClient =
            HttpUsersClient(RestTemplate(), jalgoarenaApiUrl)
}
