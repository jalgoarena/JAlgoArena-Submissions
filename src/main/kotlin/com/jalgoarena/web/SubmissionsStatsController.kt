package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.SubmissionStats
import com.jalgoarena.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
class SubmissionsStatsController(
        @Autowired private val usersClient: UsersClient,
        @Autowired private val submissionsRepository: SubmissionsRepository
) {

    @GetMapping("/submissions/stats", produces = ["application/json"])
    fun submissionsStats(): Map<String, SubmissionStats> {
        val submissions = submissionsRepository.findAll()
        val users = usersClient.findAllUsers()
        return buildStatsFrom(submissions, users)
    }

    private fun buildStatsFrom(submissions: List<Submission>, users: List<User>): Map<String, SubmissionStats> {
        val stats = mutableMapOf<String, SubmissionStats>()

        submissions.stream()
                .sorted { a, b -> a.submissionTime.compareTo(b.submissionTime) }
                .forEach { submission ->
                    val username = users.first { it.id == submission.userId }.username

                    if (!stats.containsKey(username)) {
                        stats[username] = SubmissionStats(
                                submissions = mutableMapOf(),
                                solvedProblemsPerDay = mutableMapOf(),
                                solved = mutableSetOf()
                        )
                    }

                    appendSubmissions(submission, stats[username]!!)
                    appendSolvedProblemsInTime(submission, stats[username]!!)
                }

        return stats
    }

    private fun appendSolvedProblemsInTime(submission: Submission, userStats: SubmissionStats) {
        if (submission.statusCode != "ACCEPTED" || userStats.solved.contains(submission.problemId)) {
            return
        }

        userStats.solved.add(submission.problemId)
        val date = extractDate(submission)

        if (userStats.solvedProblemsPerDay.containsKey(date) && userStats.solved.contains(submission.problemId)) {
            userStats.solvedProblemsPerDay[date]!!.add(submission.problemId)
        } else {
            userStats.solvedProblemsPerDay[date] = mutableSetOf(submission.problemId)
        }
    }

    private fun appendSubmissions(submission: Submission, userStats: SubmissionStats) {
        val date = extractDate(submission)

        if (userStats.submissions.containsKey(date)) {
            userStats.submissions[date] = userStats.submissions[date]!! + 1
        } else {
            userStats.submissions[date] = 1
        }
    }

    private fun extractDate(submission: Submission) =
            submission.submissionTime.format(YYYY_MM_DD)

    companion object {
        private val YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}