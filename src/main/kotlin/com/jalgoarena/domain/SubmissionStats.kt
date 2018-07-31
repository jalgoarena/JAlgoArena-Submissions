package com.jalgoarena.domain

data class SubmissionStats(
        val submissions: MutableMap<String, Int>,
        val solved: MutableSet<String>,
        val solvedProblemsPerDay: MutableMap<String, MutableSet<String>>
)
