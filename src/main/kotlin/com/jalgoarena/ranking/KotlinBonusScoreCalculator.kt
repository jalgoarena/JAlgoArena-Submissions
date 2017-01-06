package com.jalgoarena.ranking

import com.jalgoarena.domain.Submission

class KotlinBonusScoreCalculator(private val scoreCalculator: ScoreCalculator) : ScoreCalculator {

    override fun calculate(userSubmission: Submission) =
            scoreCalculator.calculate(userSubmission) * languageFactor(userSubmission)

    private fun languageFactor(userSubmission: Submission) : Double {
        return if ("kotlin" == userSubmission.language) 1.5 else 1.0
    }
}
