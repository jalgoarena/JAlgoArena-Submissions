package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
open class JAlgoArenaSubmissionsApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaSubmissionsApp::class.java, *args)
}
