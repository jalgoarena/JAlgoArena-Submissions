package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.netflix.discovery.EurekaClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.rule.KafkaEmbedded

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = ["results", "submissions"], controlledShutdown = true)
class JAlgoArenaSubmissionsAppIntegrationTest {
    @Inject
    private lateinit var restTemplate: TestRestTemplate

    @MockBean
    private lateinit var submissionsRepository: SubmissionsRepository

    @MockBean
    private lateinit var eurekaClient: EurekaClient

    @Inject
    private lateinit var embeddedKafka: KafkaEmbedded

    @Test
    fun check_if_spring_configuration_works_properly() {
        val body = this.restTemplate.getForObject("/info", String::class.java)
        assertThat(body).isEqualTo("{}")
    }

    @After
    fun tearDown() {
        embeddedKafka.after()
    }
}
