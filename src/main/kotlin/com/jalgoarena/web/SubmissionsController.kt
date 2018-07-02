package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.SubmissionStats
import com.jalgoarena.domain.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
class SubmissionsController(
        @Inject private val usersClient: UsersClient,
        @Inject private val submissionsRepository: SubmissionsRepository
) {

    @GetMapping("/submissions/{userId}", produces = ["application/json"])
    fun findUserSubmissions(
            @PathVariable userId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            user.id != userId -> unauthorized()
            else -> {
                ok(submissionsRepository.findByUserId(user.id))
            }
        }
    }

    @GetMapping("/submissions/find/{userId}/{submissionId}", produces = ["application/json"])
    fun findUserSubmissionBySubmissionId(
            @PathVariable userId: String,
            @PathVariable submissionId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            user.id != userId -> unauthorized()
            else -> {
                ok(submissionsRepository.findBySubmissionId(submissionId))
            }
        }
    }

    @GetMapping("/submissions/stats", produces = ["application/json"])
    fun submissionStats(): ResponseEntity<SubmissionStats> {
        return ok(stats(submissionsRepository))
    }

    private fun stats(submissionsRepository: SubmissionsRepository): SubmissionStats {
        val count = mutableMapOf<String, MutableMap<String, Int>>()
        val submissions = submissionsRepository.findAll()

        submissions.forEach { submission ->
            if (!count.contains(submission.userId)) {
                count[submission.userId] = mutableMapOf()
            }

            if (!count[submission.userId]!!.contains(submission.problemId)) {
                count[submission.userId]!![submission.problemId] = count[submission.userId]!![submission.problemId]!! + 1
            } else {
                count[submission.userId]!![submission.problemId] = 1
            }
        }

        return SubmissionStats(count)
    }

    private fun <T> checkUser(token: String?, action: (User) -> ResponseEntity<T>): ResponseEntity<T> {
        if (token == null) {
            return unauthorized()
        }

        val user = usersClient.findUser(token) ?: return unauthorized()
        return action(user)
    }

    private fun <T> unauthorized(): ResponseEntity<T> = ResponseEntity(HttpStatus.UNAUTHORIZED)
}
