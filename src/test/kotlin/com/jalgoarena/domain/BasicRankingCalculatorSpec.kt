package com.jalgoarena.domain

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.ranking.BasicRankingCalculator
import com.jalgoarena.ranking.BasicScoreCalculator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.time.LocalDateTime

class BasicRankingCalculatorSpec {

    private lateinit var submissionsRepository: SubmissionsRepository
    private lateinit var problemsRepository: ProblemsRepository

    @Before
    fun setUp() {
        submissionsRepository = mock(SubmissionsRepository::class.java)
        problemsRepository = mock(ProblemsRepository::class.java)
    }

    @Test
    fun returns_empty_ranking_if_no_users() {
        given(submissionsRepository.findAll()).willReturn(emptyList())

        val rankingCalculator = basicRankingCalculator(submissionsRepository)

        assertThat(rankingCalculator.ranking(emptyList(), submissionsRepository.findAll(), problemsRepository.findAll())).isEqualTo(emptyList<RankEntry>())
    }

    @Test
    fun returns_all_users_with_0_score_if_no_submissions() {
        given(submissionsRepository.findAll()).willReturn(emptyList())

        val rankingCalculator = basicRankingCalculator(submissionsRepository)

        assertThat(rankingCalculator.ranking(USERS, submissionsRepository.findAll(), problemsRepository.findAll())).isEqualTo(listOf(
                RankEntry("mikołaj", 0.0, emptyList(), "Kraków", "Tyniec Team", emptyList()),
                RankEntry("julia", 0.0, emptyList(), "Kraków", "Tyniec Team", emptyList()),
                RankEntry("joe", 0.0, emptyList(), "London", "London Team", emptyList()),
                RankEntry("tom", 0.0, emptyList(), "London", "London Team", emptyList())
        ))
    }

    @Test
    fun returns_users_in_descending_order_based_on_their_score_and_if_user_equal_following_creation_of_user_order() {
        given(problemsRepository.findAll()).willReturn(listOf(
                Problem("fib", 1, 1),
                Problem("2-sum", 2, 1),
                Problem("word-ladder", 3, 1)
        ))

        given(submissionsRepository.findAll()).willReturn(listOf(
                submission("fib", 0.01, USER_MIKOLAJ.id),
                submission("fib", 0.011, USER_JULIA.id),
                submission("2-sum", 0.01, USER_JOE.id),
                submission("2-sum", 0.011, USER_TOM.id),
                submission("word-ladder", 0.01, USER_MIKOLAJ.id),
                submission("word-ladder", 0.011, USER_JULIA.id)
        ))

        val rankingCalculator = basicRankingCalculator(submissionsRepository)

        assertThat(rankingCalculator.ranking(USERS, submissionsRepository.findAll(), problemsRepository.findAll())).isEqualTo(listOf(
                RankEntry("mikołaj", 60.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team", listOf(Pair("java", 2))),
                RankEntry("julia", 60.0, listOf("fib", "word-ladder"), "Kraków", "Tyniec Team", listOf(Pair("java", 2))),
                RankEntry("joe", 30.0, listOf("2-sum"), "London", "London Team", listOf(Pair("java", 1))),
                RankEntry("tom", 30.0, listOf("2-sum"), "London", "London Team", listOf(Pair("java", 1)))
        ))
    }

    @Test
    fun returns_empty_problem_ranking_when_no_submissions_for_problem() {
        given(submissionsRepository.findByProblemId("fib")).willReturn(emptyList())

        val rankingCalculator = basicRankingCalculator(submissionsRepository)

        assertThat(rankingCalculator.problemRanking("fib", USERS, problemsRepository.findAll()))
                .isEqualTo(emptyList<ProblemRankEntry>())
    }

    @Test
    fun returns_problem_ranking_sorted_by_times() {
        given(problemsRepository.findAll()).willReturn(listOf(
                Problem("fib", 1, 1)
        ))

        given(submissionsRepository.findByProblemId("fib")).willReturn(listOf(
                submission("fib", 0.01, USER_MIKOLAJ.id),
                submission("fib", 0.0001, USER_JULIA.id),
                submission("fib", 0.001, USER_JOE.id),
                submission("fib", 0.1, USER_TOM.id)
        ))

        val rankingCalculator = basicRankingCalculator(submissionsRepository)

        assertThat(rankingCalculator.problemRanking("fib", USERS, problemsRepository.findAll())).isEqualTo(listOf(
                ProblemRankEntry("julia", 10.0, 0.0001, "java"),
                ProblemRankEntry("joe", 10.0, 0.001, "java"),
                ProblemRankEntry("mikołaj", 10.0, 0.01, "java"),
                ProblemRankEntry("tom", 10.0, 0.1, "java")
        ))
    }

    private fun basicRankingCalculator(repository: SubmissionsRepository) =
            BasicRankingCalculator(repository, BasicScoreCalculator())

    private fun submission(problemId: String, elapsedTime: Double, userId: String) =
            Submission(
                    problemId,
                    DUMMY_SOURCE_CODE,
                    STATUS_ACCEPTED,
                    userId,
                    "java",
                    "2",
                    LocalDateTime.now().toString(),
                    elapsedTime,
                    10L,
                    null,
                    1,
                    0
            )

    companion object {
        private val USER_MIKOLAJ = User("mikołaj", "Kraków", "Tyniec Team", "USER", "0-0")
        private val USER_JULIA = User("julia", "Kraków", "Tyniec Team", "USER", "0-1")
        private val USER_JOE = User("joe", "London", "London Team", "USER", "0-2")
        private val USER_TOM = User("tom", "London", "London Team", "USER", "0-3")

        private val USERS = listOf(USER_MIKOLAJ, USER_JULIA, USER_JOE, USER_TOM)

        private const val DUMMY_SOURCE_CODE = "dummy source code"
        private const val STATUS_ACCEPTED = "ACCEPTED"
    }
}
