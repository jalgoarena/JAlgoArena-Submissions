package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jetbrains.exodus.entitystore.Entity
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class Submission(
        val problemId: String,
        val sourceCode: String,
        val statusCode: String,
        val userId: String,
        val language: String,
        val submissionId: String,
        val submissionTime: String,
        val elapsedTime: Double,
        val consumedMemory: Long,
        val errorMessage: String?,
        val passedTestCases: Int?,
        val failedTestCases: Int?,
        val token: String? = null,
        var id: String? = null) {


    companion object {
        fun from(entity: Entity): Submission {
            return Submission(
                    entity.getProperty(Constants.problemId) as String,
                    entity.getProperty(Constants.sourceCode) as String,
                    entity.getProperty(Constants.statusCode) as String,
                    entity.getProperty(Constants.userId) as String,
                    entity.getProperty(Constants.language) as String,
                    entity.getProperty(Constants.submissionId) as String,
                    entity.getProperty(Constants.submissionTime) as String,
                    entity.getProperty(Constants.elapsedTime) as Double,
                    entity.getProperty(Constants.consumedMemory) as Long,
                    entity.getProperty(Constants.errorMessage) as String?,
                    entity.getProperty(Constants.passedTestCases) as Int?,
                    entity.getProperty(Constants.failedTestCases) as Int?,
                    id = entity.id.toString()
            )
        }

        fun notFound(submissionId: String): Submission {
            return Submission(
                    "",
                    "",
                    "NOT_FOUND",
                    "",
                    "",
                    submissionId,
                    LocalDateTime.now().toString(),
                    -1.0,
                    0L,
                    "Couldn't find submission",
                    0,
                    0
            )
        }
    }
}
