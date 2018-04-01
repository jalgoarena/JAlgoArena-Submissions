package com.jalgoarena.data

import com.jalgoarena.domain.SubmissionInProgress

interface SubmissionsInProgressRepository {
    fun findAll(): List<SubmissionInProgress>
    fun findByUserId(userId: String): List<SubmissionInProgress>
    fun findById(id: String): SubmissionInProgress?
    fun findByProblemId(problemId: String): List<SubmissionInProgress>
    fun addOrUpdate(submissionInProgress: SubmissionInProgress): SubmissionInProgress
    fun delete(id: String): List<SubmissionInProgress>
    fun destroy()
}