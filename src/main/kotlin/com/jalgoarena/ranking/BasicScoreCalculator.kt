package com.jalgoarena.ranking

import com.jalgoarena.domain.Submission

class BasicScoreCalculator : ScoreCalculator {
    override fun calculate(userSubmission: Submission) =
            userSubmission.level * timeFactor(userSubmission.elapsedTime)

    private fun timeFactor(elapsedTime: Double) =
            if (elapsedTime >= 500) 1.0
            else if (elapsedTime >= 100) 3.0
            else if (elapsedTime >= 10) 5.0
            else if (elapsedTime >= 1) 8.0
            else 10.0
}
