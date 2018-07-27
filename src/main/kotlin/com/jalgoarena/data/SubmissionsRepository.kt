package com.jalgoarena.data

import com.jalgoarena.domain.Submission
import org.springframework.data.jpa.repository.JpaRepository

interface SubmissionsRepository : JpaRepository<Submission, Int> {
    fun findByUserId(userId: String): List<Submission>
    fun findBySubmissionId(submissionId: String): Submission
}

