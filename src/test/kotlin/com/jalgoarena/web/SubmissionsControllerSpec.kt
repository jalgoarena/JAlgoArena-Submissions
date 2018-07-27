package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@WebFluxTest(SubmissionsController::class)
class SubmissionsControllerSpec {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var usersClient: UsersClient

    @MockBean
    private lateinit var submissionRepository: SubmissionsRepository

    @Test
    fun returns_401_if_user_is_not_authorized_get_submissions_for_user_id() {
        webTestClient.get().uri("/submissions/${USER.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun returns_401_if_user_is_the_same_as_submissions_owner_get_submissions_for_user_id() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)

        webTestClient.get().uri("/submissions/differentuser")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun returns_401_when_user_is_unidentified_for_get_submissions_for_user_id() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(null)

        webTestClient.get().uri("/submissions/${USER.id}")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun returns_200_and_all_submissions_for_get_submissions_for_user_id() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)
        given(usersClient.findAllUsers()).willReturn(emptyList())

        val userSubmissions = listOf(submission(USER.id, 0))
        given(submissionRepository.findByUserId(USER.id)).willReturn(userSubmissions)

        webTestClient.get().uri("/submissions/${USER.id}")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
    }

    @Test
    fun returns_200_and_empty_stats_for_no_submissions() {
        given(submissionRepository.findAll()).willReturn(emptyList())

        webTestClient.get().uri("/submissions/stats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(String::class.java)
                .consumeWith {
                    assertThat(it.responseBody.blockFirst()).isEqualTo("{\"count\":{}}")
                }
    }

    @Test
    fun returns_200_and_stats_for_single_user_single_submissions() {
        val userSubmissions = listOf(submission(USER.id, 0))
        given(submissionRepository.findAll()).willReturn(userSubmissions)

        webTestClient.get().uri("/submissions/stats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(String::class.java)
                .consumeWith {
                    assertThat(it.responseBody.blockFirst()).isEqualTo("{\"count\":{\"0-0\":{\"fib\":1}}}")
                }
    }

    @Test
    fun returns_200_and_stats_for_single_user_many_submissions() {
        val userSubmissions = listOf(submission(USER.id, 0), submission(USER.id, 1))
        given(submissionRepository.findAll()).willReturn(userSubmissions)

        webTestClient.get().uri("/submissions/stats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(String::class.java)
                .consumeWith {
                    assertThat(it.responseBody.blockFirst()).isEqualTo("{\"count\":{\"0-0\":{\"fib\":2}}}")
                }
    }

    @Test
    fun returns_200_and_stats_for_many_user_many_submissions() {
        val userSubmissions = listOf(submission(USER.id, 0), submission(USER.id, 1), submission("0-1", 2))
        given(submissionRepository.findAll()).willReturn(userSubmissions)

        webTestClient.get().uri("/submissions/stats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(String::class.java)
                .consumeWith {
                    assertThat(it.responseBody.blockFirst()).isEqualTo("{\"count\":{\"0-0\":{\"fib\":2},\"0-1\":{\"fib\":1}}}")
                }
    }

    companion object {

        private val USER = User("julia", "Kraków", "Tyniec Team", "USER", "0-0")
        private const val DUMMY_TOKEN = "Bearer 123j12n31lkmdp012j21d"

        private fun submission(userId: String, id: Int? = null) =
                submissionForProblem("fib", userId, id)

        private fun submissionForProblem(problemId: String, userId: String, id: Int? = null) =
                Submission(
                        problemId,
                        "class Solution",
                        "ACCEPTED", userId,
                        "2",
                        LocalDateTime.now().toString(),
                        0.5,
                        10L,
                        null,
                        1,
                        0,
                        null,
                        id
                )
    }
}
