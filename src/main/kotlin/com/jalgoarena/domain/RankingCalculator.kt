package com.jalgoarena.domain

interface RankingCalculator {
    fun ranking(users: Array<User>): List<RankEntry>
    fun problemRanking(problemId: String, users: Array<User>): List<ProblemRankEntry>
}
