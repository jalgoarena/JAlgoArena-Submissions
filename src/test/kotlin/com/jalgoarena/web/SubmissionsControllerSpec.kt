package com.jalgoarena.web

import com.fasterxml.jackson.databind.node.ArrayNode
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.User
import org.hamcrest.Matchers.hasSize
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import javax.inject.Inject

@RunWith(SpringRunner::class)
@WebMvcTest(SubmissionsController::class)
class SubmissionsControllerSpec {

    @Inject
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usersClient: UsersClient

    @MockBean
    private lateinit var submissionRepository: SubmissionsRepository

    @Test
    fun returns_401_if_user_is_not_authorized_get_submissions_for_user_id() {
        mockMvc.perform(get("/submissions/${USER.id}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_if_user_is_the_same_as_submissions_owner_get_submissions_for_user_id() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)

        mockMvc.perform(get("/submissions/differentuser")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_when_user_is_unidentified_for_get_submissions_for_user_id() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(null)

        mockMvc.perform(get("/submissions/${USER.id}")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_200_and_all_submissions_for_get_submissions_for_user_id() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)
        given(usersClient.findAllUsers()).willReturn(emptyList())

        val userSubmissions = listOf(submission(USER.id, "0-0"))
        given(submissionRepository.findByUserId(USER.id)).willReturn(userSubmissions)

        mockMvc.perform(get("/submissions/${USER.id}")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(1)))
    }

    companion object {

        private val USER = User("julia", "Kraków", "Tyniec Team", "USER", "0-0")
        private const val DUMMY_TOKEN = "Bearer 123j12n31lkmdp012j21d"

        private fun submission(userId: String, id: String? = null) =
                submissionForProblem("fib", userId, id)

        private fun submissionForProblem(problemId: String, userId: String, id: String? = null) =
                Submission(
                        problemId,
                        "class Solution",
                        "ACCEPTED", userId,
                        "java",
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
