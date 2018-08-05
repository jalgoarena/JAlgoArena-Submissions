package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.RankingSubmission
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessResourceUsageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.transaction.TransactionException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
class SubmissionsController(
        @Autowired private val usersClient: UsersClient,
        @Autowired private val submissionsRepository: SubmissionsRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/submissions", produces = ["application/json"])
    fun findAll(): List<RankingSubmission> = try {
        submissionsRepository.findAll().map {
            RankingSubmission(it.id!!, it.problemId, it.statusCode, it.userId, it.submissionTime, it.elapsedTime)
        }
    } catch (e: TransactionException) {
        logger.error("Cannot connect to database", e)
        listOf()
    } catch (e: InvalidDataAccessResourceUsageException) {
        logger.error("Wrong database schema", e)
        listOf()
    }

    @GetMapping("/submissions/problem/{problemId}", produces = ["application/json"])
    fun findAllForProblem(
            @PathVariable problemId: String
    ): List<RankingSubmission> = try {
        submissionsRepository.findByProblemId(problemId).map {
            RankingSubmission(it.id!!, it.problemId, it.statusCode, it.userId, it.submissionTime, it.elapsedTime)
        }
    } catch (e: TransactionException) {
        logger.error("Cannot connect to database", e)
        listOf()
    } catch (e: InvalidDataAccessResourceUsageException) {
        logger.error("Wrong database schema", e)
        listOf()
    }

    @GetMapping("/submissions/date/{date}", produces = ["application/json"])
    fun findAllForDate(
            @PathVariable date: String
    ): List<RankingSubmission> = try {
        val tillDate = LocalDate.parse(date, YYYY_MM_DD).plusDays(1).atStartOfDay()
        submissionsRepository.findBySubmissionTimeLessThan(tillDate).map {
            RankingSubmission(it.id!!, it.problemId, it.statusCode, it.userId, it.submissionTime, it.elapsedTime)
        }
    } catch (e: TransactionException) {
        logger.error("Cannot connect to database", e)
        listOf()
    } catch (e: InvalidDataAccessResourceUsageException) {
        logger.error("Wrong database schema", e)
        listOf()
    }

    @GetMapping("/submissions/{userId}", produces = ["application/json"])
    fun findAllUserSubmissions(
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

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @GetMapping("/submissions/find/{userId}/{submissionId}", produces = ["application/json"])
    fun findUserSubmissionBySubmissionId(
            @PathVariable userId: String,
            @PathVariable submissionId: String,
            @RequestHeader("X-Authorization", required = false) token: String?
    ) = checkUser(token) { user ->
        when {
            user.id != userId -> unauthorized()
            else -> {
                val submission = submissionsRepository.findBySubmissionId(submissionId)
                ok(submission)
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

    private fun <T> unauthorized(): ResponseEntity<T> =
            ResponseEntity(HttpStatus.UNAUTHORIZED)

    companion object {
        private val YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}
