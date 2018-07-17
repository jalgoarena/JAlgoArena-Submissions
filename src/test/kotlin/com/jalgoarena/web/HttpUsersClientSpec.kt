package com.jalgoarena.web

import com.jalgoarena.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

class HttpUsersClientSpec {

    private val restTemplate = mock(RestOperations::class.java)
    private val usersClient = HttpUsersClient(restTemplate, "http://jalgoarena-api")

    @Test
    fun delegates_get_user_request_to_auth_service_endpoint() {

        val headers = HttpHeaders().apply {
            set("X-Authorization", DUMMY_TOKEN)
        }

        val entity = HttpEntity<HttpHeaders>(headers)


        given(restTemplate.exchange(
                "http://jalgoarena-api/auth/api/user", HttpMethod.GET, entity, User::class.java
        )).willReturn(ResponseEntity.ok(USER))

        val user = usersClient.findUser(DUMMY_TOKEN)

        assertThat(user).isEqualTo(USER)
    }

    @Test
    fun returns_null_if_there_is_any_issue_with_response() {

        val user = usersClient.findUser(DUMMY_TOKEN)
        assertThat(user).isNull()
    }

    @Test
    fun returns_all_users() {

        given(restTemplate.getForObject("http://jalgoarena-api/auth/users", Array<User>::class.java))
                .willReturn(arrayOf(USER))

        val users = usersClient.findAllUsers()

        assertThat(users).hasSize(1)
    }

    companion object {
        private const val DUMMY_TOKEN = "Bearer sdaw9awdw"
        private const val USER_ROLE = "USER"
        private val USER = User("mikolaj", "Kraków", "Tyniec Team", USER_ROLE, "0-0")
    }
}
