package com.jalgoarena.web

import com.jalgoarena.data.ProblemsRepository
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Constants.ADMIN_ROLE
import com.jalgoarena.domain.User
import com.jalgoarena.ranking.RankingCalculator
import com.jalgoarena.ranking.SolvedRatioCalculator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
class SubmissionsController(
        @Inject private val rankingCalculator: RankingCalculator,
        @Inject private val usersClient: UsersClient,
        @Inject private val problemsRepository: ProblemsRepository,
        @Inject private val submissionsRepository: SubmissionsRepository
) : SolvedRatioCalculator {


    @GetMapping("/submissions", produces = arrayOf("application/json"))
    fun submissions(
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role -> unauthorized()
            else -> ok(submissionsRepository.findAll())
        }
    }

    @GetMapping("/submissions/solved-ratio", produces = arrayOf("application/json"))
    fun submissionsSolvedRatio() = calculateSubmissionsSolvedRatioAndReturnIt(submissionsRepository.findAll())

    @DeleteMapping("/submissions/{submissionId}", produces = arrayOf("application/json"))
    fun deleteSubmission(
            @PathVariable submissionId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            ADMIN_ROLE != user.role -> unauthorized()
            else -> ok(submissionsRepository.delete(submissionId))
        }
    }

    @GetMapping("/submissions/{userId}", produces = arrayOf("application/json"))
    fun userSubmissions(
            @PathVariable userId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            user.id != userId -> unauthorized()
            else -> {
                val problems = problemsRepository.findAll()
                val users = usersClient.findAllUsers()

                ok(rankingCalculator.userRankingDetails(user, problems, users))
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
