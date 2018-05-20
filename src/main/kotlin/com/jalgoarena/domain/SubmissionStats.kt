package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SubmissionStats(
        val count: Map<String, Map<String, Map<String, Int>>>
)