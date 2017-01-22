package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class SolvedRatioEntrySerializationTest {
    private lateinit var json: JacksonTester<SolvedRatioEntry>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_solved_ratio_entry() {
        assertThat(json.write(SOLVED_RATIO_ENTRY))
                .isEqualToJson("solved-ratio-entry.json")
    }

    @Test
    fun should_deserialize_solved_ratio_entry() {
        assertThat(json.parse(SOLVED_RATIO_ENTRY_JSON))
                .isEqualTo(SOLVED_RATIO_ENTRY)
    }

    private val SOLVED_RATIO_ENTRY = SolvedRatioEntry(
            problemId = "fib",
            solutionsCount = 10
    )

    @Language("JSON")
    private val SOLVED_RATIO_ENTRY_JSON = """{
  "problemId":  "fib",
  "solutionsCount":  10
}
"""
}
