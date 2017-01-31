package com.jalgoarena.ranking

import com.jalgoarena.domain.Problem
import com.jalgoarena.domain.Submission
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class KotlinBonusScoreCalculatorSpec {

    private lateinit var calculator: ScoreCalculator

    @Before
    fun setUp() {
        calculator = mock(ScoreCalculator::class.java)
    }

    @Test
    fun returns_150_percent_of_normal_score_for_kotlin() {
        val userSubmission = submission("kotlin")
        given(calculator.calculate(userSubmission, listOf(Problem("fib", 1)))).willReturn(10.0)

        val kotlinBonusScoreCalculator = KotlinBonusScoreCalculator(calculator)
        val score = kotlinBonusScoreCalculator.calculate(userSubmission, listOf(Problem("fib", 1)))

        assertThat(score).isEqualTo(15.0)
    }

    @Test
    fun returns_unchanged_score_for_non_kotlin_language() {
        val userSubmission = submission("java")
        val initialScore = 10.0

        given(calculator.calculate(userSubmission, listOf(Problem("fib", 1)))).willReturn(initialScore)

        val kotlinBonusScoreCalculator = KotlinBonusScoreCalculator(calculator)
        val score = kotlinBonusScoreCalculator.calculate(userSubmission, listOf(Problem("fib", 1)))

        assertThat(score).isEqualTo(10.0)
    }

    private fun submission(language: String) =
            Submission("fib", 0.1, "DUMMY", "ACCEPTED", "0-0", language)
}
