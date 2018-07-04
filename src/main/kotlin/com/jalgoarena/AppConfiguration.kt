package com.jalgoarena


import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.util.StringUtils
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.beans.factory.annotation.Autowired



@Configuration
open class AppConfiguration {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var discoveryClient: DiscoveryClient

    @Bean
    open fun kafkaAdmin(): KafkaAdmin {
        val kafka1 = retrieveKafkaAddress("kafka1")
        val kafka2 = retrieveKafkaAddress("kafka2")
        val kafka3 = retrieveKafkaAddress("kafka3")

        val bootstrapServers = listOf(kafka1, kafka2, kafka3).filter { it.isNotBlank() }.toTypedArray()

        logger.info("Configuring kafka brokers with: $bootstrapServers")

        val config = mutableMapOf<String, Any>()

        config[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] =
                StringUtils.arrayToCommaDelimitedString(bootstrapServers)

        return KafkaAdmin(config)
    }

    @Bean
    open fun submissionsTopic(): NewTopic {
        return NewTopic("submissions", 9, 3)
    }

    private fun retrieveKafkaAddress(kafkaInstanceName: String): String {
        val kafka1Instances = discoveryClient.getInstances(kafkaInstanceName)
        if (kafka1Instances.isEmpty()) {
            return NO_KAFKA_INSTANCE
        }

        val uri = kafka1Instances[1].uri
        return "${uri.host}:${uri.port}"
    }

    @LoadBalanced
    @Bean
    open fun restTemplate(): RestOperations = RestTemplate()

    companion object {
        private const val NO_KAFKA_INSTANCE = ""
    }
}
