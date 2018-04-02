package com.jalgoarena.web

import com.fasterxml.jackson.databind.node.ArrayNode
import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionResultsRepository
import com.jalgoarena.domain.SubmissionResult
import com.jalgoarena.domain.SubmissionWithRankingDetails
import com.jalgoarena.domain.User
import com.jalgoarena.ranking.RankingCalculator
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.inject.Inject

@RunWith(SpringRunner::class)
@WebMvcTest(SubmissionsController::class)
class SubmissionsControllerSpec {

    @Inject
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usersClient: UsersClient

    @MockBean
    private lateinit var submissionRepository: SubmissionResultsRepository

    @MockBean
    private lateinit var problemsRepository: ProblemsRepository

    @MockBean
    private lateinit var rankingCalculator: RankingCalculator

    @Test
    fun returns_200_and_submissions_solved_ratio_list() {
        given(submissionRepository.findAll()).willReturn(listOf(
                submissionForProblem("fib", "user1"),
                submissionForProblem("fib", "user2"),
                submissionForProblem("fib", "user3"),
                submissionForProblem("2-sum", "user1"),
                submissionForProblem("2-sum", "user2")
        ))

        mockMvc.perform(get("/submissions/solved-ratio")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(2)))
                .andExpect(jsonPath("$[0].problemId", `is`("fib")))
                .andExpect(jsonPath("$[0].solutionsCount", `is`(3)))
    }

    @Test
    fun returns_401_if_user_is_not_authorized_for_get_submissions() {
        mockMvc.perform(get("/submissions")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_if_user_is_not_admin_for_get_submissions() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)

        mockMvc.perform(get("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_when_user_is_unidentified_for_get_submissions() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(null)

        mockMvc.perform(get("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_200_and_all_submissions_to_admin_for_get_submissions() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(ADMIN)

        given(submissionRepository.findAll()).willReturn(listOf(
            submission("mikołaj"),
            submission("julia"),
            submission("madzia")
        ))

        mockMvc.perform(get("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(3)))
    }

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

        given(problemsRepository.findAll()).willReturn(emptyList())

        given(rankingCalculator.userRankingDetails(USER, emptyList(), emptyList())).willReturn(listOf(
                SubmissionWithRankingDetails.from(userSubmissions[0], 2, 15.0, 2)
        ))

        mockMvc.perform(get("/submissions/${USER.id}")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(1)))
    }

    @Test
    fun returns_401_if_user_is_not_authorized_for_delete_submission_request() {
        mockMvc.perform(delete("/submissions/0-0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_if_user_is_not_admin_for_delete_submission_request() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)

        mockMvc.perform(delete("/submissions/0-0")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_when_user_is_unidentified_for_delete_submission_request() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(null)

        mockMvc.perform(delete("/submissions/0-0")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_200_and_true_to_admin_for_successful_delete_submission_request() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(ADMIN)
        given(submissionRepository.delete("0-0")).willReturn(emptyList())

        mockMvc.perform(delete("/submissions/0-0")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(0)))
    }

    private val USER = User("julia", "Kraków", "Tyniec Team", "USER", "0-0")
    private val ADMIN = User("", "", "", "ADMIN", "")

    private val DUMMY_TOKEN = "Bearer 123j12n31lkmdp012j21d"

    private fun submission(userId: String, id: String? = null) =
            submissionForProblem("fib", userId, id)

    private fun submissionForProblem(problemId: String, userId: String, id: String? = null) =
            SubmissionResult(
                    problemId,
                    0.5,
                    "class Solution",
                    "ACCEPTED", userId,
                    "java",
                    "2",
                    10L,
                    null,
                    emptyList(),
                    null,
                    id
            )

    private fun submissionJson(userId: String) = """{
  "problemId": "fib",
  "level": 1,
  "elapsedTime": 0.5,
  "sourceCode": "class Solution",
  "statusCode": "ACCEPTED",
  "userId": "$userId",
  "language": "java"
}
"""
}
