package com.jalgoarena.ranking

import com.jalgoarena.domain.Problem
import com.jalgoarena.domain.Submission
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(JUnitParamsRunner::class)
class BasicScoreCalculatorSpec {

    private val calculator = BasicScoreCalculator()

    @Test
    @Parameters(
            "0.1, 10.0",
            "0.99, 10.0",
            "1.0, 8.0",
            "9.99, 8.0",
            "10.0, 5.0",
            "99.99, 5.0",
            "100.0, 3.0",
            "499.99, 3.0",
            "500.00, 1.0",
            "9999.99, 1.0"
    )
    fun return_score_based_on_elapsed_time(elapsedTime: Double, expectedScore: Double) {
        val submission = submission(
                elapsedTime = elapsedTime
        )

        val result = calculator.calculate(submission, Problem("fib", 1, 1))
        assertThat(result).isEqualTo(expectedScore)
    }

    @Test
    @Parameters(
            "1, 10.0",
            "2, 30.0",
            "3, 50.0"
    )
    fun return_score_based_on_level(level: Int, expectedScore: Double) {
        val submission = submission(
                elapsedTime = 0.1
        )

        val result = calculator.calculate(submission, Problem("fib", level, 1))
        assertThat(result).isEqualTo(expectedScore)
    }

    private fun submission(elapsedTime: Double) =
            Submission(
                    "fib",
                    DUMMY_SOURCE_CODE,
                    STATUS_ACCEPTED,
                    "0-0",
                    "java",
                    "2",
                    LocalDateTime.now().toString(),
                    elapsedTime,
                    10L,
                    null,
                    1,
                    0,
                    null
            )

    companion object {
        private const val DUMMY_SOURCE_CODE = "dummy source code"
        private const val STATUS_ACCEPTED = "ACCEPTED"
    }
}
