package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class RankEntrySerializationTest {

    private lateinit var json: JacksonTester<RankEntry>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_rank_entry() {
        assertThat(json.write(RANK_ENTRY))
                .isEqualToJson("rank-entry.json")
    }

    @Test
    fun should_deserialize_rank_entry() {
        assertThat(json.parse(RANK_ENTRY_JSON))
                .isEqualTo(RANK_ENTRY)
    }

    private val RANK_ENTRY = RankEntry(
            hacker = "julia",
            score = 14.5,
            region = "Kraków",
            team = "Tyniec Team",
            solvedProblems = listOf("fib", "2-sum")
    )

    private val RANK_ENTRY_JSON = """{
  "hacker":  "julia",
  "score":  14.5,
  "region":  "Kraków",
  "team":  "Tyniec Team",
  "solvedProblems":  ["fib", "2-sum"]
}
"""
}
