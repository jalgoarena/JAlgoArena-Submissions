package com.jalgoarena.domain

import com.jalgoarena.data.SubmissionsRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class BasicRankingCalculator(@Inject val repository: SubmissionsRepository) : RankingCalculator {

    override fun ranking(users: Array<User>): List<RankEntry> {
        val submissions = repository.findAll()

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
        val problemSubmissions = repository.findByProblemId(problemId)

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

    private fun score(userSubmissions: List<Submission>): Double {
        fun timeFactor(elapsedTime: Double) =
                if (elapsedTime > 500) 1
                else if (elapsedTime > 100) 3
                else if (elapsedTime > 10) 5
                else if (elapsedTime >= 1) 8
                else 10

        return userSubmissions.sumByDouble {
            val languageFactor = if ("kotlin" == it.language) 1.5 else 1.0
            it.level * timeFactor(it.elapsedTime) * languageFactor
        }
    }
}
