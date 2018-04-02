package com.jalgoarena.ranking

import com.jalgoarena.domain.Problem
import com.jalgoarena.domain.SubmissionResult

interface ScoreCalculator {
    fun calculate(userSubmission: SubmissionResult, problem: Problem): Double
}
