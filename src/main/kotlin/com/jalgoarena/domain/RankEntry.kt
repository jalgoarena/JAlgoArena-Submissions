package com.jalgoarena.domain

data class RankEntry(
        val hacker: String,
        val score: Double,
        val solvedProblems: List<String>,
        val region: String,
        val team: String,
        val numberOfSolutionsPerLanguage: List<Pair<String, Int>>
)
