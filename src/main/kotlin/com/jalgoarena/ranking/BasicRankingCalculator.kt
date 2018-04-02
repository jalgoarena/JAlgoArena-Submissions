package com.jalgoarena.ranking

import com.jalgoarena.data.SubmissionResultsRepository
import com.jalgoarena.domain.*

class BasicRankingCalculator(
        submissionResultsRepository: SubmissionResultsRepository,
        scoreCalculator: ScoreCalculator
) : RankingCalculator,
        ScoreCalculator by scoreCalculator,
        SubmissionResultsRepository by submissionResultsRepository {

    override fun userRankingDetails(
            user: User,
            problems: List<Problem>,
            users: List<User>
    ) = findByUserId(user.id).map { userSubmission ->

        val problem = problems.first { it.id == userSubmission.problemId }
        val score = calculate(userSubmission, problem)
        val (problemRankPlace) = problemRanking(problem.id, users, problems)
                .mapIndexed { i, problemRankEntry -> Pair(i, problemRankEntry) }
                .first { it.second.hacker == user.username }

        SubmissionWithRankingDetails.from(
                userSubmission,
                problem.level,
                score,
                problemRankPlace + 1
        )
    }

    override fun ranking(users: List<User>, submissionResults: List<SubmissionResult>, problems: List<Problem>): List<RankEntry> {

        return users.map { user ->
            val userSubmissions = submissionResults.filter { it.userId == user.id }
            val solvedProblems = userSubmissions.map { it.problemId }
            val numberOfSolutionsPerLanguage = userSubmissions
                    .groupBy { it.language }
                    .map { Pair(it.key, it.value.size) }

            RankEntry(
                    user.username,
                    score(userSubmissions, problems),
                    solvedProblems,
                    user.region,
                    user.team,
                    numberOfSolutionsPerLanguage
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

    private fun score(userSubmissions: List<SubmissionResult>, problems: List<Problem>): Double {
        return userSubmissions.sumByDouble { userSubmission ->
            val problem = problems.first { it.id == userSubmission.problemId }
            calculate(userSubmission, problem)
        }
    }
}

