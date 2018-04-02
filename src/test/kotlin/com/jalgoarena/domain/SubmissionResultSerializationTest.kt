package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class SubmissionResultSerializationTest {

    private lateinit var json: JacksonTester<SubmissionResult>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_submission() {
        assertThat(json.write(SUBMISSION))
                .isEqualToJson("submission-result.json")
    }

    @Test
    fun should_deserialize_submission() {
        assertThat(json.parse(SUBMISSION_JSON))
                .isEqualTo(SUBMISSION)
    }

    private val SUBMISSION = SubmissionResult(
            problemId = "fib",
            elapsedTime = 435.212,
            sourceCode = "dummy source code",
            statusCode = "ACCEPTED",
            userId = "0-0",
            language = "kotlin",
            id = "2-4",
            submissionId = "2",
            consumedMemory = 10L,
            errorMessage = null,
            testcaseResults = emptyList(),
            token = null
    )

    @Language("JSON")
    private val  SUBMISSION_JSON = """{
  "problemId": "fib",
  "elapsedTime": 435.212,
  "sourceCode": "dummy source code",
  "statusCode": "ACCEPTED",
  "userId": "0-0",
  "language": "kotlin",
  "id": "2-4",
  "submissionId": "2",
  "consumedMemory": 10,
  "testcaseResults": []
}
"""
}
