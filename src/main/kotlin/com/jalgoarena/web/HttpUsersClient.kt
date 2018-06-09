package com.jalgoarena.web

import com.jalgoarena.domain.User
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import javax.inject.Inject

@Service
class HttpUsersClient(
        @Inject private val restTemplate : RestOperations
) : UsersClient {

    override fun findAllUsers() = handleExceptions(returnOnException = emptyList()) {
        restTemplate.getForObject(
                "jalgoarena-auth/users", Array<User>::class.java)!!.asList()
    }

    override fun findUser(token: String) = handleExceptions(returnOnException = null) {
        val headers = HttpHeaders().apply {
            set("X-Authorization", token)
        }

        val entity = HttpEntity<HttpHeaders>(headers)

        val response = restTemplate.exchange(
                "jalgoarena-auth/api/user", HttpMethod.GET, entity, User::class.java)
        response.body
    }

    private fun <T> handleExceptions(returnOnException: T, body: () -> T) = try {
        body()
    } catch(e: Exception) {
        LOG.error("Error in querying jalgoarena auth service", e)
        returnOnException
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(HttpUsersClient::class.java)
    }
}
