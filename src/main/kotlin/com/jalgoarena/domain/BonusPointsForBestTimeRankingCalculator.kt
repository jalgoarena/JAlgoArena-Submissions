package com.jalgoarena.domain

import com.jalgoarena.data.SubmissionsRepository
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class BonusPointsForBestTimeRankingCalculator(
        @Inject val repository: SubmissionsRepository,
        val rankingCalculator: RankingCalculator
) : RankingCalculator {

    override fun ranking(users: Array<User>): List<RankEntry> {
        val submissions = repository.findAll()

        val bonusPoints = calculateBonusPointsForFastestSolutions(submissions, users)

        return rankingCalculator.ranking(users).map { rankEntry ->
            val id = users.first { it.username == rankEntry.hacker }.id

            RankEntry(
                    rankEntry.hacker,
                    rankEntry.score + bonusPoints[id] as Double,
                    rankEntry.solvedProblems,
                    rankEntry.region,
                    rankEntry.team
            )
        }.sortedByDescending { it.score }
    }

    override fun problemRanking(problemId: String, users: Array<User>): List<ProblemRankEntry> {
        val problemSubmissions = repository.findByProblemId(problemId)

        val bonusPoints = calculateBonusPointsForFastestSolutions(problemSubmissions, users)

        return rankingCalculator.problemRanking(problemId, users).map { problemRankEntry ->
            val user = users.filter { it.username == problemRankEntry.hacker }.first()

            ProblemRankEntry(
                    problemRankEntry.hacker,
                    problemRankEntry.score + bonusPoints[user.id] as Double,
                    problemRankEntry.elapsedTime,
                    problemRankEntry.language
            )
        }.sortedBy { it.elapsedTime }
    }

    private fun calculateBonusPointsForFastestSolutions(submissions: List<Submission>, users: Array<User>): Map<String, Double> {

        val bonusPoints = mutableMapOf<String, Double>()
        users.forEach { bonusPoints[it.id] = 0.0 }

        val problems = submissions.map { it.problemId }.distinct()

        problems.forEach { problem ->
            val problemSubmissions = submissions.filter { it.problemId == problem }
            val fastestSubmission = problemSubmissions.minBy { it.elapsedTime }

            if (fastestSubmission != null) {
                bonusPoints[fastestSubmission.userId] = bonusPoints[fastestSubmission.userId] as Double + 1.0
            }
        }

        return bonusPoints
    }
}
