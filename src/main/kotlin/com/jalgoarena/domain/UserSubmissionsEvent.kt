package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserSubmissionsEvent(
        val type: String = "refreshUserSubmissions",
        val userId: String,
        val problemId: String,
        val submissionId: String
)