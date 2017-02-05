package com.jalgoarena.ranking

import com.jalgoarena.domain.Problem
import com.jalgoarena.domain.Submission

class KotlinBonusScoreCalculator(private val baseScoreCalculator: ScoreCalculator) : ScoreCalculator {

    override fun calculate(userSubmission: Submission, problem: Problem) =
            baseScoreCalculator.calculate(userSubmission, problem) * languageFactor(userSubmission)

    private fun languageFactor(userSubmission: Submission) : Double {
        return if ("kotlin" == userSubmission.language) 1.5 else 1.0
    }
}
