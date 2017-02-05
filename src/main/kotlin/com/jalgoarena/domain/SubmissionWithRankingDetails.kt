package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmissionWithRankingDetails (
        val problemId: String,
        val elapsedTime: Double,
        val sourceCode: String,
        val statusCode: String,
        val language: String,
        val id: String,
        val level: Int,
        val score: Double,
        val problemRankPlace: Int) {

    companion object {
        fun from (submission: Submission, level: Int, score: Double, problemRankPlace: Int) = SubmissionWithRankingDetails(
                submission.problemId,
                submission.elapsedTime,
                submission.sourceCode,
                submission.statusCode,
                submission.language,
                submission.id!!,
                level,
                score,
                problemRankPlace
        )
    }
}
