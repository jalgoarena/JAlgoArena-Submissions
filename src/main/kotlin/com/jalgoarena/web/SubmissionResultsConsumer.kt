package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Constants.ADMIN_ROLE
import com.jalgoarena.domain.Submission
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class SubmissionResultsConsumer(
        @Inject private val submissionsRepository: SubmissionsRepository,
        @Inject private val usersClient: UsersClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(topics = ["results"])
    fun storeSubmission(message: String) {

        val submission = toSubmission(message)

        logger.info("Received {} [submissionId={}]", "Submission result", submission.submissionId)

        if (isValidUser(submission)) {
            submissionsRepository.addOrUpdate(submission)
            logger.info("Submission result is saved [submissionId={}]", submission.submissionId)

        } else {
            logger.warn(
                    "Cannot store Submission result [submissionId={}] - authentication failed",
                    submission.submissionId,
                    submission.userId
            )
        }
    }

    private fun toSubmission(message: String): Submission {
        return jacksonObjectMapper().readValue<Submission>(message, Submission::class.java)
    }

    private fun isValidUser(submission: Submission): Boolean {
        val token = submission.token

        if (token == null) {
            logger.warn("Token is empty!")
            return false
        }

        val user = usersClient.findUser(token)

        if (user == null) {
            logger.warn("No user with id: {}", submission.userId)
            return false
        }

        val result = ADMIN_ROLE == user.role || user.id == submission.userId

        return if (result) {
            true
        } else {
            logger.warn("Your are not ADMIN nor the authenticated user is not an owner of submission")
            false
        }
    }
}
