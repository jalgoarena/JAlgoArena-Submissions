package com.jalgoarena.domain

import com.jalgoarena.data.SubmissionsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class BonusPointsForBestTimeRankingCalculatorSpec {

    private lateinit var repository: SubmissionsRepository
    private lateinit var rankingCalculator: RankingCalculator

    @Before
    fun setUp() {
        repository = mock(SubmissionsRepository::class.java)
        rankingCalculator = mock(RankingCalculator::class.java)
    }

    @Test
    fun returns_users_in_descending_order_based_on_score_when_fastests_solution_per_problem_has_1_bonus_point_more() {
        given(repository.findAll()).willReturn(listOf(
                javaSubmission("fib", 1, 0.01, USER_MIKOLAJ.id),
                kotlinSubmission("fib", 1, 0.011, USER_JULIA.id),
                javaSubmission("2-sum", 2, 0.01, USER_JOE.id),
                kotlinSubmission("2-sum", 2, 0.011, USER_TOM.id),
                javaSubmission("word-ladder", 3, 0.01, USER_MIKOLAJ.id),
                kotlinSubmission("word-ladder", 3, 0.011, USER_JULIA.id)
        ))

        val rankingCalculator = bonusPointsForBestTimeRankingCalculator(repository)

        assertThat(rankingCalculator.ranking(USERS)).isEqualTo(listOf(
                RankEntry("julia", 60.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team"),
                RankEntry("mikołaj", 42.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team"),
                RankEntry("tom", 30.0, listOf("2-sum"), "London", "London Team"),
                RankEntry("joe", 21.0, listOf("2-sum"), "London", "London Team")
        ))
    }

    @Test
    fun returns_1_additional_point_for_fastest_solution() {
        given(repository.findAll()).willReturn(listOf(
                kotlinSubmission("fib", 1, 0.01, USER_JULIA.id),
                kotlinSubmission("fib", 1, 0.001, USER_JOE.id)
        ))

        val rankingCalculator = bonusPointsForBestTimeRankingCalculator(repository)

        assertThat(rankingCalculator.ranking(USERS)).isEqualTo(listOf(
                RankEntry("joe", 16.0, listOf("fib"), "London", "London Team"),
                RankEntry("julia", 15.0, listOf("fib"), "Kraków", "Tyniec Team"),
                RankEntry("mikołaj", 0.0, emptyList(), "Kraków", "Tyniec Team"),
                RankEntry("tom", 0.0, emptyList(), "London", "London Team")
        ))
    }

    private fun bonusPointsForBestTimeRankingCalculator(repository: SubmissionsRepository) =
            BonusPointsForBestTimeRankingCalculator(repository, BasicRankingCalculator(repository))

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
