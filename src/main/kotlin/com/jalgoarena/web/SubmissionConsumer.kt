package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.UserSubmissionsEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SubmissionConsumer(
        @Autowired private val submissionsRepository: SubmissionsRepository,
        @Autowired private val usersClient: UsersClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var template: KafkaTemplate<Int, UserSubmissionsEvent>

    @KafkaListener(topics = ["submissions"])
    fun storeSubmission(message: String) {

        val submission = toSubmission(message)

        logger.info("Received submission [submissionId={}][status={}]",
                submission.submissionId,
                submission.statusCode)

        if (isValidUser(submission)) {
            submissionsRepository.addOrUpdate(submission)
            logger.info("Submission is saved [submissionId={}]", submission.submissionId)

            val future = template.send("events", UserSubmissionsEvent(userId = submission.userId))
            future.addCallback(SubmissionResultsConsumer.PublishHandler(submission.submissionId, "new submission"))

        } else {
            logger.warn(
                    "Cannot store Submission [submissionId={}] - authentication failed",
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

        return if (user.id == submission.userId) {
            true
        } else {
            logger.warn("Your are not ADMIN nor the authenticated user is not an owner of submission")
            false
        }
    }

}
