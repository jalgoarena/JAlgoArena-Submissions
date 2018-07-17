package com.jalgoarena

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@Configuration
open class AppConfiguration {

    @Bean
    open fun restTemplate(): RestOperations = RestTemplate()
}
