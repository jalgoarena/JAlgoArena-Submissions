package com.jalgoarena.domain

data class SubmissionStats(
        val username: String,
        val submissions: MutableMap<String, Int>,
        val solved: MutableSet<Any>
)
