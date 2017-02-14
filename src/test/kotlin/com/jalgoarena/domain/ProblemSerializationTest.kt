package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class ProblemSerializationTest {
    private lateinit var json: JacksonTester<Problem>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_problem() {
        Assertions.assertThat(json.write(PROBLEM))
                .isEqualToJson("problem.json")
    }

    @Test
    fun should_deserialize_problem() {
        Assertions.assertThat(json.parse(PROBLEM_JSON))
                .isEqualTo(PROBLEM)
    }

    private val PROBLEM = Problem(
            id = "fib",
            level = 1,
            timeLimit = 1
    )

    @Language("JSON")
    private val  PROBLEM_JSON = """{
  "id": "fib",
  "level": 1,
  "timeLimit": 1
}
"""
}
