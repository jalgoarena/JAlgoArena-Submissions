package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jetbrains.exodus.entitystore.Entity

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmissionInProgress(
        val sourceCode: String,
        val userId: String,
        val language: String,
        val submissionId: String,
        val problemId: String,
        var state: String?,
        val token: String?,
        var id: String? = null
) {
    companion object {
        fun from(entity: Entity): SubmissionInProgress {
            return SubmissionInProgress(
                    entity.getProperty(Constants.sourceCode) as String,
                    entity.getProperty(Constants.userId) as String,
                    entity.getProperty(Constants.language) as String,
                    entity.getProperty(Constants.submissionId) as String,
                    entity.getProperty(Constants.problemId) as String,
                    entity.getProperty(Constants.state) as String,
                    null,
                    entity.id.toString()
            )
        }
    }
}