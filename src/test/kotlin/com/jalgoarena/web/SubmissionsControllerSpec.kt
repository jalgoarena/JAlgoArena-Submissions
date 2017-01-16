package com.jalgoarena.web

import com.fasterxml.jackson.databind.node.ArrayNode
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.User
import org.hamcrest.Matchers.*
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
    private lateinit var submissionRepository: SubmissionsRepository

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

        given(submissionRepository.findByUserId(USER.id)).willReturn(listOf(
                submission(USER.id)
        ))

        mockMvc.perform(get("/submissions/${USER.id}")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(1)))
    }

    @Test
    fun returns_401_if_user_is_not_authorized_for_put_submissions() {
        mockMvc.perform(put("/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(submissionJson(USER.id)))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_if_user_is_the_same_as_submission_owner_for_put_submissions() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)

        mockMvc.perform(put("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submissionJson("differentuserid")))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_401_when_user_is_unidentified_for_put_submissions() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(null)

        mockMvc.perform(put("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submissionJson(USER.id)))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_200_and_newly_added_submission_for_put_submissions() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(USER)

        given(submissionRepository.addOrUpdate(submission(USER.id))).willReturn(
                submission(USER.id, "0-1")
        )

        mockMvc.perform(put("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submissionJson(USER.id)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", `is`("0-1")))
    }

    @Test
    fun returns_200_and_re_added_submission_for_put_submissions_if_user_is_admin() {
        given(usersClient.findUser(DUMMY_TOKEN)).willReturn(ADMIN)

        given(submissionRepository.addOrUpdate(submission(USER.id))).willReturn(
                submission(USER.id, "0-1")
        )

        mockMvc.perform(put("/submissions")
                .header("X-Authorization", DUMMY_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(submissionJson(USER.id)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", `is`("0-1")))
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
            Submission("fib", 1, 0.5, "class Solution", "ACCEPTED", userId, "java", id)

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
