package com.jalgoarena.ranking

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.*

class BasicRankingCalculator(
        submissionsRepository: SubmissionsRepository,
        scoreCalculator: ScoreCalculator
) : RankingCalculator,
        ScoreCalculator by scoreCalculator,
        SubmissionsRepository by submissionsRepository {

    override fun ranking(users: List<User>, submissions: List<Submission>, problems: List<Problem>): List<RankEntry> {

        return users.map { user ->
            val userSubmissions = submissions.filter { it.userId == user.id }
            val solvedProblems = userSubmissions.map { it.problemId }

            RankEntry(
                    user.username,
                    score(userSubmissions, problems),
                    solvedProblems,
                    user.region,
                    user.team
            )
        }.sortedByDescending { it.score }
    }

    override fun problemRanking(problemId: String, users: List<User>, problems: List<Problem>): List<ProblemRankEntry> {
        val problemSubmissions = findByProblemId(problemId)

        return problemSubmissions.map { submission ->
            val user = users.filter { it.id == submission.userId }.first()

            ProblemRankEntry(
                    user.username,
                    score(listOf(submission), problems),
                    submission.elapsedTime,
                    submission.language
            )
        }.sortedBy { it.elapsedTime }
    }

    private fun score(userSubmissions: List<Submission>, problems: List<Problem>): Double {
        return userSubmissions.sumByDouble {  calculate(it, problems) }
    }
}

