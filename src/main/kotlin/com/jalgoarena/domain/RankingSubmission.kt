package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class RankingSubmission(
        val id: Int,
        val problemId: String,
        val statusCode: String,
        val userId: String,
        val submissionTime: LocalDateTime,
        val elapsedTime: Double
)
