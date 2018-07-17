package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
@EnableCaching(proxyTargetClass=true)
open class JAlgoArenaSubmissionsApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaSubmissionsApp::class.java, *args)
}
