package com.jalgoarena.ranking

import com.jalgoarena.domain.SolvedRatioEntry
import com.jalgoarena.domain.Submission

interface SolvedRatioCalculator {
    fun calculateSubmissionsSolvedRatioAndReturnIt(submissions: List<Submission>) = submissions
            .groupBy { it.problemId }
            .map { SolvedRatioEntry(it.key, it.value.count())
    }
}
