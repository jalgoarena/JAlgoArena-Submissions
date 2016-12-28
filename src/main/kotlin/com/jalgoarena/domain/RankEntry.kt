package com.jalgoarena.domain

data class RankEntry(
        val hacker: String,
        val score: Double,
        val solvedProblems: List<String>,
        val region: String,
        val team: String
)

data class ProblemRankEntry(
        val hacker: String,
        val score: Double,
        val elapsedTime: Double,
        val language: String
)
