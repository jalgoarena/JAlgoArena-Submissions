package com.jalgoarena.domain

import com.jalgoarena.data.SubmissionsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class BasicRankingCalculatorSpec {

    private lateinit var repository: SubmissionsRepository

    @Before
    fun setUp() {
        repository = mock(SubmissionsRepository::class.java)
    }

    @Test
    fun returns_empty_ranking_if_no_users() {
        given(repository.findAll()).willReturn(emptyList())

        val rankingCalculator = BasicRankingCalculator(repository)

        assertThat(rankingCalculator.ranking(emptyArray())).isEqualTo(emptyList<RankEntry>())
    }

    @Test
    fun returns_all_users_with_0_score_if_no_submissions() {
        given(repository.findAll()).willReturn(emptyList())

        val rankingCalculator = BasicRankingCalculator(repository)

        assertThat(rankingCalculator.ranking(USERS)).isEqualTo(listOf(
                RankEntry("mikołaj", 0.0, emptyList(), "Kraków", "Tyniec Team"),
                RankEntry("julia", 0.0, emptyList(), "Kraków", "Tyniec Team"),
                RankEntry("joe", 0.0, emptyList(), "London", "London Team"),
                RankEntry("tom", 0.0, emptyList(), "London", "London Team")
        ))
    }

    @Test
    fun returns_users_in_descending_order_based_on_their_score() {
        given(repository.findAll()).willReturn(listOf(
                javaSubmission("fib", 1, 0.01, USER_MIKOLAJ.id),
                kotlinSubmission("fib", 1, 0.011, USER_JULIA.id),
                javaSubmission("2-sum", 2, 0.01, USER_JOE.id),
                kotlinSubmission("2-sum", 2, 0.011, USER_TOM.id),
                javaSubmission("word-ladder", 3, 0.01, USER_MIKOLAJ.id),
                kotlinSubmission("word-ladder", 3, 0.011, USER_JULIA.id)
        ))

        val rankingCalculator = BasicRankingCalculator(repository)

        assertThat(rankingCalculator.ranking(USERS)).isEqualTo(listOf(
                RankEntry("julia", 60.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team"),
                RankEntry("mikołaj", 40.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team"),
                RankEntry("tom", 30.0, listOf("2-sum"), "London", "London Team"),
                RankEntry("joe", 20.0, listOf("2-sum"), "London", "London Team")
        ))
    }

    @Test
    fun returns_150_precent_points_of_java_solution_for_kotlin_with_same_time() {
        given(repository.findAll()).willReturn(listOf(
                javaSubmission("fib", 1, 0.01, USER_JULIA.id),
                kotlinSubmission("fib", 1, 0.01, USER_JOE.id)
        ))

        val rankingCalculator = BasicRankingCalculator(repository)

        assertThat(rankingCalculator.ranking(USERS)).isEqualTo(listOf(
                RankEntry("joe", 15.0, listOf("fib"), "London", "London Team"),
                RankEntry("julia", 10.0, listOf("fib"), "Kraków", "Tyniec Team"),
                RankEntry("mikołaj", 0.0, emptyList(), "Kraków", "Tyniec Team"),
                RankEntry("tom", 0.0, emptyList(), "London", "London Team")
        ))
    }

    private fun javaSubmission(problemId: String, level: Int, elapsedTime: Double, userId: String) =
            Submission(problemId, level, elapsedTime, DUMMY_SOURCE_CODE, STATUS_ACCEPTED, userId, "java")

    private fun kotlinSubmission(problemId: String, level: Int, elapsedTime: Double, userId: String) =
            Submission(problemId, level, elapsedTime, DUMMY_SOURCE_CODE, STATUS_ACCEPTED, userId, "kotlin")

    private val USER_MIKOLAJ = User("mikołaj", "Kraków", "Tyniec Team", "USER", "0-0")
    private val USER_JULIA = User("julia", "Kraków", "Tyniec Team", "USER", "0-1")
    private val USER_JOE = User("joe", "London", "London Team", "USER", "0-2")
    private val USER_TOM = User("tom", "London", "London Team", "USER", "0-3")

    private val USERS = arrayOf(USER_MIKOLAJ, USER_JULIA, USER_JOE, USER_TOM)

    private val DUMMY_SOURCE_CODE = "dummy source code"
    private val STATUS_ACCEPTED = "ACCEPTED"
}
