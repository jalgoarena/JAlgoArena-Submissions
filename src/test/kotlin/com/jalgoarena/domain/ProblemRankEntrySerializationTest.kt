package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class ProblemRankEntrySerializationTest {

    private lateinit var json: JacksonTester<ProblemRankEntry>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_problem_rank_entry() {
        assertThat(json.write(PROBLEM_RANK_ENTRY))
                .isEqualToJson("problem-rank-entry.json")
    }

    @Test
    fun should_deserialize_problem_rank_entry() {
        assertThat(json.parse(PROBLEM_RANK_ENTRY_JSON))
                .isEqualTo(PROBLEM_RANK_ENTRY)
    }

    private val PROBLEM_RANK_ENTRY = ProblemRankEntry(
            hacker = "mikołaj",
            language = "java",
            elapsedTime = 0.12345,
            score = 22.5
    )

    private val PROBLEM_RANK_ENTRY_JSON = """{
  "hacker": "mikołaj",
  "language": "java",
  "elapsedTime": 0.12345,
  "score": 22.5
}
"""
}
