package com.jalgoarena.ranking

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.ProblemRankEntry
import com.jalgoarena.domain.RankEntry
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.User

class BasicRankingCalculator(
        repository: SubmissionsRepository,
        scoreCalculator: ScoreCalculator
) : RankingCalculator,
        ScoreCalculator by scoreCalculator,
        SubmissionsRepository by repository {

    override fun ranking(users: Array<User>): List<RankEntry> {
        val submissions = findAll()

        return users.map { user ->
            val userSubmissions = submissions.filter { it.userId == user.id }
            val solvedProblems = userSubmissions.map { it.problemId }

            RankEntry(
                    user.username,
                    score(userSubmissions),
                    solvedProblems,
                    user.region,
                    user.team
            )
        }.sortedByDescending { it.score }
    }

    override fun problemRanking(problemId: String, users: Array<User>): List<ProblemRankEntry> {
        val problemSubmissions = findByProblemId(problemId)

        return problemSubmissions.map { submission ->
            val user = users.filter { it.id == submission.userId }.first()

            ProblemRankEntry(
                    user.username,
                    score(listOf(submission)),
                    submission.elapsedTime,
                    submission.language
            )
        }.sortedBy { it.elapsedTime }
    }

    fun score(userSubmissions: List<Submission>): Double {
        return userSubmissions.sumByDouble {  calculate(it) }
    }
}

