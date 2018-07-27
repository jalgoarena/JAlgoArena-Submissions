package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "submissions")
data class Submission(
        @Column(nullable = false)
        var problemId: String = "",
        @Column(nullable = false, length = 20000)
        var sourceCode: String = "",
        @Column(nullable = false)
        var statusCode: String = "NOT_FOUND",
        @Column(nullable = false)
        var userId: String = "",
        @Column(unique = true, nullable = false)
        var submissionId: String = "",
        @Column(nullable = false)
        var submissionTime: String = LocalDateTime.now().toString(),
        @Column(nullable = false)
        var elapsedTime: Double = -1.0,
        @Column(nullable = false)
        var consumedMemory: Long = 0L,
        var errorMessage: String? = null,
        var passedTestCases: Int? = 0,
        var failedTestCases: Int? = 0,
        var token: String? = null,
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int? = null
)
