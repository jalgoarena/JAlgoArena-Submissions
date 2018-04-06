package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class SubmissionSerializationTest {

    private lateinit var json: JacksonTester<Submission>

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

    @Test
    fun make_sure_queue_submission_can_be_deserialized() {
        val queueSubmission = QueueSubmission(
                SUBMISSION.sourceCode,
                SUBMISSION.userId,
                SUBMISSION.language,
                SUBMISSION.submissionId,
                SUBMISSION.submissionTime,
                SUBMISSION.problemId,
                SUBMISSION.token,
                "WAITING"
        )

        val json = jacksonObjectMapper().writeValueAsString(queueSubmission)
        val submission = jacksonObjectMapper().readValue<Submission>(json, Submission::class.java)

        assertThat(submission.statusCode).isEqualTo("WAITING")
    }

    companion object {
        private val SUBMISSION = Submission(
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
                submissionTime = "",
                passedTestCases = 1,
                failedTestCases = 0,
                token = null
        )

        @Language("JSON")
        private val SUBMISSION_JSON = """{
  "problemId": "fib",
  "elapsedTime": 435.212,
  "sourceCode": "dummy source code",
  "statusCode": "ACCEPTED",
  "userId": "0-0",
  "language": "kotlin",
  "id": "2-4",
  "submissionId": "2",
  "submissionTime": "",
  "consumedMemory": 10,
  "passedTestCases": 1,
  "failedTestCases": 0
}
"""
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class QueueSubmission(
            val sourceCode: String,
            val userId: String,
            val language: String,
            val submissionId: String,
            val submissionTime: String,
            val problemId: String,
            val token: String?,
            val statusCode: String
    )
}
