package com.jalgoarena.web

import com.fasterxml.jackson.databind.node.ArrayNode
import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.ProblemRankEntry
import com.jalgoarena.domain.RankEntry
import com.jalgoarena.ranking.RankingCalculator
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
import javax.inject.Inject

@RunWith(SpringRunner::class)
@WebMvcTest(RankingController::class)
class RankingControllerSpec {

    @Inject
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usersClient: UsersClient

    @MockBean
    private lateinit var rankingCalculator: RankingCalculator

    @MockBean
    private lateinit var problemsRepository: ProblemsRepository

    @MockBean
    private lateinit var submissionsRepository: SubmissionsRepository

    @Test
    fun returns_200_and_ranking() {
        given(usersClient.findAllUsers()).willReturn(emptyList())
        given(submissionsRepository.findAll()).willReturn(emptyList())
        given(problemsRepository.findAll()).willReturn(emptyList())

        given(rankingCalculator.ranking(emptyList(), emptyList(), emptyList())).willReturn(listOf(
                RankEntry("mikołaj", 40.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team", emptyList()),
                RankEntry("julia", 40.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team", emptyList()),
                RankEntry("joe", 20.0, listOf("2-sum"), "London", "London Team", emptyList()),
                RankEntry("tom", 20.0, listOf("2-sum"), "London", "London Team", emptyList())
        ))

        mockMvc.perform(get("/ranking")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(4)))
    }

    @Test
    fun returns_200_and_problem_ranking() {
        given(usersClient.findAllUsers()).willReturn(emptyList())
        given(problemsRepository.findAll()).willReturn(emptyList())

        given(rankingCalculator.problemRanking("fib", emptyList(), emptyList())).willReturn(listOf(
                ProblemRankEntry("julia", 10.0, 0.0001, "java"),
                ProblemRankEntry("joe", 10.0, 0.001, "java"),
                ProblemRankEntry("mikołaj", 10.0, 0.01, "java")
        ))

        mockMvc.perform(get("/ranking/fib")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(3)))
    }
}
