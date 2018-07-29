package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester
import java.time.LocalDateTime
import java.time.Month

class SubmissionSerializationTest {

    private lateinit var json: JacksonTester<Submission>
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setup() {
        objectMapper = jacksonObjectMapper()
        objectMapper.findAndRegisterModules()
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
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

    @Test
    fun make_sure_queue_submission_can_be_deserialized() {
        val queueSubmission = QueueSubmission(
                SUBMISSION.sourceCode,
                SUBMISSION.userId,
                SUBMISSION.submissionId,
                SUBMISSION.submissionTime,
                SUBMISSION.problemId,
                SUBMISSION.token,
                "WAITING"
        )

        val json = objectMapper.writeValueAsString(queueSubmission)
        val submission = objectMapper.readValue<Submission>(json, Submission::class.java)

        assertThat(submission.statusCode).isEqualTo("WAITING")
    }

    companion object {
        private val SUBMISSION_TIME = LocalDateTime.of(
                2018, Month.JULY, 29, 8, 1, 1
        )

        private val SUBMISSION = Submission(
                problemId = "fib",
                elapsedTime = 435.212,
                sourceCode = "dummy source code",
                statusCode = "ACCEPTED",
                userId = "0-0",
                id = 2,
                submissionId = "2",
                consumedMemory = 10L,
                errorMessage = null,
                submissionTime = SUBMISSION_TIME,
                passedTestCases = 1,
                failedTestCases = 0,
                token = "dummy_token"
        )

        @Language("JSON")
        private val SUBMISSION_JSON = """{
  "problemId": "fib",
  "elapsedTime": 435.212,
  "sourceCode": "dummy source code",
  "statusCode": "ACCEPTED",
  "userId": "0-0",
  "id": 2,
  "submissionId": "2",
  "submissionTime": "2018-07-29T08:01:01",
  "consumedMemory": 10,
  "passedTestCases": 1,
  "failedTestCases": 0,
  "token": "dummy_token"
}
"""
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class QueueSubmission(
            val sourceCode: String,
            val userId: String,
            val submissionId: String,
            val submissionTime: LocalDateTime,
            val problemId: String,
            val token: String?,
            val statusCode: String
    )
}
