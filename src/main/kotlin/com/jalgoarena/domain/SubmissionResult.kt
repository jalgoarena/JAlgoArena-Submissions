package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jetbrains.exodus.entitystore.Entity

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmissionResult(
        val problemId: String,
        val elapsedTime: Double,
        val sourceCode: String,
        val statusCode: String,
        val userId: String,
        val language: String,
        val submissionId: String,
        val consumedMemory: Long,
        val errorMessage: String?,
        val testcaseResults: List<Boolean>,
        val token: String?,
        var id: String? = null) {


    companion object {
        fun from(entity: Entity): SubmissionResult {
            val testcaseResults = (entity.getProperty(Constants.testcaseResults) as String)
                    .split(";")
                    .map(String::toBoolean)

            return SubmissionResult(
                    entity.getProperty(Constants.problemId) as String,
                    entity.getProperty(Constants.elapsedTime) as Double,
                    entity.getProperty(Constants.sourceCode) as String,
                    entity.getProperty(Constants.statusCode) as String,
                    entity.getProperty(Constants.userId) as String,
                    entity.getProperty(Constants.language) as String,
                    entity.getProperty(Constants.submissionId) as String,
                    entity.getProperty(Constants.consumedMemory) as Long,
                    entity.getProperty(Constants.errorMessage) as String?,
                    testcaseResults,
                    null,
                    entity.id.toString()
            )
        }

        fun notFound(submissionId: String): SubmissionResult {
            return SubmissionResult(
                    "",
                    0.0,
                    "",
                    "NOT_FOUND",
                    "",
                    "",
                    submissionId,
                    0L,
                    "Couldn't find submission",
                    emptyList(),
                    null
            )
        }
    }
}
