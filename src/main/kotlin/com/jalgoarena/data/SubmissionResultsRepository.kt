package com.jalgoarena.data

import com.jalgoarena.domain.SubmissionResult

interface SubmissionResultsRepository {
    fun findAll(): List<SubmissionResult>
    fun findByUserId(userId: String): List<SubmissionResult>
    fun findById(id: String): SubmissionResult?
    fun findByProblemId(problemId: String): List<SubmissionResult>
    fun addOrUpdate(submissionResult: SubmissionResult): SubmissionResult
    fun delete(id: String): List<SubmissionResult>
    fun destroy()
    fun findBySubmissionId(submissionId: String): SubmissionResult
}

