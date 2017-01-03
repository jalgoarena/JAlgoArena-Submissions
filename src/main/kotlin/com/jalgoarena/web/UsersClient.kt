package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.ApiGatewayConfiguration
import com.jalgoarena.domain.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class UsersClient(@Inject val apiGatewayConfiguration: ApiGatewayConfiguration) {

    private fun authServiceUrl() = "${apiGatewayConfiguration.apiGatewayUrl}/auth"

    private val httpClient = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun findAllUsers(): Array<User> {

        val request = Request.Builder()
            .url("${authServiceUrl()}/users")
            .build()

        val response = httpClient.newCall(request).execute()
        return objectMapper.readValue(response.body().string(), Array<User>::class.java)
    }

    fun findUser(token: String): User {
        val request = Request.Builder()
                .url("${authServiceUrl()}/api/user")
                .addHeader("X-Authorization", token)
                .build()

        val response = httpClient.newCall(request).execute()
        return objectMapper.readValue(response.body().string(), User::class.java)
    }
}
