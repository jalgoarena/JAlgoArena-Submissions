package com.jalgoarena.ranking

import com.jalgoarena.domain.Submission
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

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
                level = 1,
                elapsedTime = elapsedTime
        )

        val result = calculator.calculate(submission)
        assertThat(result).isEqualTo(expectedScore)
    }

    @Test
    @Parameters(
            "1, 10.0",
            "2, 20.0",
            "3, 30.0"
    )
    fun return_score_based_on_level(level: Int, expectedScore: Double) {
        val submission = submission(
                level = level,
                elapsedTime = 0.1
        )

        val result = calculator.calculate(submission)
        assertThat(result).isEqualTo(expectedScore)
    }

    private fun submission(level: Int, elapsedTime: Double) =
            Submission("fib", level, elapsedTime, DUMMY_SOURCE_CODE, STATUS_ACCEPTED, "0-0", "java")

    private val DUMMY_SOURCE_CODE = "dummy source code"
    private val STATUS_ACCEPTED = "ACCEPTED"
}
