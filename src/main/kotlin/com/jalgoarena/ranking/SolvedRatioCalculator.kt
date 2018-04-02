package com.jalgoarena.ranking

import com.jalgoarena.domain.SolvedRatioEntry
import com.jalgoarena.domain.SubmissionResult

interface SolvedRatioCalculator {
    fun calculateSubmissionsSolvedRatioAndReturnIt(submissionResults: List<SubmissionResult>) = submissionResults
            .groupBy { it.problemId }
            .map { SolvedRatioEntry(it.key, it.value.count())
    }
}
