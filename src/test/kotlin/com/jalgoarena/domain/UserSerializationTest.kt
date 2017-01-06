package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester


class UserSerializationTest {

    private lateinit var json: JacksonTester<User>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_user() {
        assertThat(json.write(USER)).isEqualToJson("user.json")
    }

    @Test
    fun should_deserialize_user() {
        assertThat(json.parse(USER_JSON))
                .isEqualTo(USER)
    }

    private val USER = User("julia", "Kraków", "Tyniec Team", "USER", "0-0")

    private val USER_JSON = """{
  "username": "julia",
  "region": "Kraków",
  "team": "Tyniec Team",
  "role": "USER",
  "id": "0-0"
}
"""
}
