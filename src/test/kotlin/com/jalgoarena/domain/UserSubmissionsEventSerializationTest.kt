package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class UserSubmissionsEventSerializationTest {

    private lateinit var json: JacksonTester<UserSubmissionsEvent>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_submission() {
        assertThat(json.write(EVENT))
                .isEqualToJson("user-submission-event-result.json")
    }

    @Test
    fun should_deserialize_submission() {
        assertThat(json.parse(EVENT_JSON))
                .isEqualTo(EVENT)
    }

    companion object {
        private val EVENT = UserSubmissionsEvent(
                userId = "0-0",
                problemId = "0-1",
                submissionId = "0-2"
        )

        @Language("JSON")
        private val EVENT_JSON = """{
  "userId": "0-0",
  "problemId": "0-1",
  "submissionId": "0-2",
  "type": "refreshUserSubmissions"
}
"""
    }
}
