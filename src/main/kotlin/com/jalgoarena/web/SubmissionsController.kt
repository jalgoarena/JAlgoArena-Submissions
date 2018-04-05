package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Constants.ADMIN_ROLE
import com.jalgoarena.domain.User
import com.jalgoarena.ranking.SolvedRatioCalculator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
class SubmissionsController(
        @Inject private val usersClient: UsersClient,
        @Inject private val submissionsRepository: SubmissionsRepository
) : SolvedRatioCalculator {


    @GetMapping("/submissions", produces = ["application/json"])
    fun submissions(
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role -> unauthorized()
            else -> ok(submissionsRepository.findAll())
        }
    }

    @GetMapping("/submissions/solved-ratio", produces = ["application/json"])
    fun submissionsSolvedRatio() = calculateSubmissionsSolvedRatioAndReturnIt(submissionsRepository.findAll())

    @DeleteMapping("/submissions/{submissionId}", produces = ["application/json"])
    fun deleteSubmission(
            @PathVariable submissionId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role -> unauthorized()
            else -> ok(submissionsRepository.delete(submissionId))
        }
    }

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

    private fun <T> checkUser(token: String?, action: (User) -> ResponseEntity<T>): ResponseEntity<T> {
        if (token == null) {
            return unauthorized()
        }

        val user = usersClient.findUser(token) ?: return unauthorized()
        return action(user)
    }

    private fun <T> unauthorized(): ResponseEntity<T> = ResponseEntity(HttpStatus.UNAUTHORIZED)
}
