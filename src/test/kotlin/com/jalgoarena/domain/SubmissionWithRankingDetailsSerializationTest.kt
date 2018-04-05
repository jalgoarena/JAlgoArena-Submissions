package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class SubmissionWithRankingDetailsSerializationTest {
    private lateinit var json: JacksonTester<SubmissionWithRankingDetails>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_submission() {
        Assertions.assertThat(json.write(SUBMISSION_WITH_RANKING_DETAILS))
                .isEqualToJson("submission-with-ranking-details.json")
    }

    @Test
    fun should_deserialize_submission() {
        Assertions.assertThat(json.parse(SUBMISSION_WITH_RANKING_DETAILS_JSON))
                .isEqualTo(SUBMISSION_WITH_RANKING_DETAILS)
    }

    private val SUBMISSION_WITH_RANKING_DETAILS = SubmissionWithRankingDetails(
            problemId = "fib",
            elapsedTime = 435.212,
            sourceCode = "dummy source code",
            statusCode = "ACCEPTED",
            language = "kotlin",
            id = "2-4",
            problemRankPlace = 2,
            level = 1,
            score = 15.0
    )

    @Language("JSON")
    private val SUBMISSION_WITH_RANKING_DETAILS_JSON = """{
  "problemId": "fib",
  "elapsedTime": 435.212,
  "sourceCode": "dummy source code",
  "statusCode": "ACCEPTED",
  "language": "kotlin",
  "id": "2-4",
  "problemRankPlace": 2,
  "level": 1,
  "score": 15.0
}
"""
}
