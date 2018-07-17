package com.jalgoarena

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@Configuration
open class AppConfiguration {

    @LoadBalanced
    @Bean
    open fun restTemplate(): RestOperations = RestTemplate()
}
