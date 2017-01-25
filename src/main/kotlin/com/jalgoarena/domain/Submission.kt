package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jetbrains.exodus.entitystore.Entity

@JsonIgnoreProperties(ignoreUnknown = true)
data class Submission(
        val problemId: String,
        val elapsedTime: Double,
        val sourceCode: String,
        val statusCode: String,
        val userId: String,
        val language: String,
        var id: String? = null) {

    companion object {
        fun from(entity: Entity): Submission {
            return Submission(
                    entity.getProperty(Constants.problemId) as String,
                    entity.getProperty(Constants.elapsedTime) as Double,
                    entity.getProperty(Constants.sourceCode) as String,
                    entity.getProperty(Constants.statusCode) as String,
                    entity.getProperty(Constants.userId) as String,
                    entity.getProperty(Constants.language) as String,
                    entity.id.toString()
            )
        }
    }
}
