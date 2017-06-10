package com.jalgoarena.ranking

import com.jalgoarena.domain.Problem
import com.jalgoarena.domain.Submission

class BasicScoreCalculator : ScoreCalculator {

    override fun calculate(userSubmission: Submission, problem: Problem): Double {
        val (_, level, timeLimit) = problem

        val basePointsPerLevel = (level - 1) * 20
        return basePointsPerLevel + timeFactor(
                userSubmission.elapsedTime / timeLimit
        )
    }

    private fun timeFactor(elapsedTime: Double) =
            if (elapsedTime >= 500) 1.0
            else if (elapsedTime >= 100) 3.0
            else if (elapsedTime >= 10) 5.0
            else if (elapsedTime >= 1) 8.0
            else 10.0
}
