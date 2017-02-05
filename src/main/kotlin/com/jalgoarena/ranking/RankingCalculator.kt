package com.jalgoarena.ranking

import com.jalgoarena.domain.*

interface RankingCalculator {
    fun ranking(users: List<User>, submissions: List<Submission>, problems: List<Problem>): List<RankEntry>
    fun problemRanking(problemId: String, users: List<User>, problems: List<Problem>): List<ProblemRankEntry>

    fun userRankingDetails(
            user: User,
            problems: List<Problem>,
            users: List<User>
    ): List<SubmissionWithRankingDetails>
}
