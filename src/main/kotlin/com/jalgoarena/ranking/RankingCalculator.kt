package com.jalgoarena.ranking

import com.jalgoarena.domain.ProblemRankEntry
import com.jalgoarena.domain.RankEntry
import com.jalgoarena.domain.User

interface RankingCalculator {
    fun ranking(users: Array<User>): List<RankEntry>
    fun problemRanking(problemId: String, users: Array<User>): List<ProblemRankEntry>
}
