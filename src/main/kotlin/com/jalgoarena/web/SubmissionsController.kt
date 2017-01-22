package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.User
import com.jalgoarena.ranking.SolvedRatioCalculator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@CrossOrigin
@RestController
class SubmissionsController(
        @Inject private val repository: SubmissionsRepository,
        @Inject private val usersClient: UsersClient): SolvedRatioCalculator {

    private val ADMIN_ROLE = "ADMIN"

    @GetMapping("/submissions", produces = arrayOf("application/json"))
    fun submissions(
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role -> unauthorized()
            else -> ok(repository.findAll())
        }
    }

    @GetMapping("/submissions/solved-ratio", produces = arrayOf("application/json"))
    fun submissionsSolvedRatio() = calculateSubmissionsSolvedRatioAndReturnIt(repository)

    @DeleteMapping("/submissions/{submissionId}", produces = arrayOf("application/json"))
    fun deleteSubmission(
            @PathVariable submissionId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role -> unauthorized()
            else -> ok(repository.delete(submissionId))
        }
    }

    @GetMapping("/submissions/{userId}", produces = arrayOf("application/json"))
    fun submissions(
            @PathVariable userId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            user.id != userId -> unauthorized()
            else -> ok(repository.findByUserId(userId))
        }
    }

    @PutMapping("/submissions", produces = arrayOf("application/json"))
    fun addOrUpdateSubmission(
            @RequestBody submission: Submission,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role && user.id != submission.userId -> unauthorized()
            else -> ResponseEntity(repository.addOrUpdate(submission), HttpStatus.CREATED)
        }
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
