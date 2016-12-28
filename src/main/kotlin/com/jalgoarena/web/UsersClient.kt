package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.domain.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class UsersClient {

    private val httpClient = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun findAllUsers(): Array<User> {
        val request = Request.Builder()
            .url("https://jalgoarena-api.herokuapp.com/auth/users")
            .build()

        val response = httpClient.newCall(request).execute()
        return objectMapper.readValue(response.body().string(), Array<User>::class.java)
    }
}
