package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.domain.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class UsersClient {

    private val API_URL = "https://jalgoarena-api.herokuapp.com/auth"

    private val httpClient = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun findAllUsers(): Array<User> {

        val request = Request.Builder()
            .url("$API_URL/users")
            .build()

        val response = httpClient.newCall(request).execute()
        return objectMapper.readValue(response.body().string(), Array<User>::class.java)
    }

    fun findUser(token: String): User {
        val request = Request.Builder()
                .url("$API_URL/api/user")
                .addHeader("X-Authorization", token)
                .build()

        val response = httpClient.newCall(request).execute()
        return objectMapper.readValue(response.body().string(), User::class.java)
    }
}
