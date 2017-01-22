package com.jalgoarena.ranking

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.SolvedRatioEntry

interface SolvedRatioCalculator {
    fun calculateSubmissionsSolvedRatioAndReturnIt(submissionsRepository: SubmissionsRepository) = submissionsRepository.findAll()
            .groupBy { it.problemId }
            .map { SolvedRatioEntry(it.key, it.value.count())
    }
}
