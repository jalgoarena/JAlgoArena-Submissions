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
        fun from (submissionResult: SubmissionResult, level: Int, score: Double, problemRankPlace: Int) = SubmissionWithRankingDetails(
                submissionResult.problemId,
                submissionResult.elapsedTime,
                submissionResult.sourceCode,
                submissionResult.statusCode,
                submissionResult.language,
                submissionResult.id!!,
                level,
                score,
                problemRankPlace
        )
    }
}
