package com.jalgoarena.data

import com.jalgoarena.domain.Submission

interface SubmissionsRepository {
    fun findAll(): List<Submission>
    fun findByUserId(userId: String): List<Submission>
    fun findById(id: String): Submission?
    fun findByProblemId(problemId: String): List<Submission>
    fun addOrUpdate(submission: Submission): Submission
    fun delete(id: String): List<Submission>
    fun destroy()
    fun findBySubmissionId(submissionId: String): Submission
    fun findAllAccepted(): List<Submission>
}

