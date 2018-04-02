package com.jalgoarena.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Constants.ADMIN_ROLE
import com.jalgoarena.domain.Submission
import com.jalgoarena.web.UsersClient
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
@KafkaListener(topics = ["submissions"])
class SubmissionConsumer(
        @Inject private val submissionsRepository: SubmissionsRepository,
        @Inject private val usersClient: UsersClient
        ) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaHandler
    fun store(message: String) {

        val submissionInProgress = jacksonObjectMapper().readValue(message, Submission::class.java)

        logger.info("Received submission [submissionId={}]", submissionInProgress.submissionId)

        if (isValidUser(submissionInProgress)) {
            submissionInProgress.state = Constants.IN_PROGRESS
            submissionsRepository.addOrUpdate(submissionInProgress)
            logger.info("SubmissionResult saved [submissionId={}]", submissionInProgress.submissionId)
        } else {
            logger.warn(
                    "Trying to store submission [submissionId={}] in progress for invalid user [userId={}]",
                    submissionInProgress.submissionId,
                    submissionInProgress.userId
            )
        }
    }

    private fun isValidUser(submission: Submission): Boolean {
        val token = submission.token ?: return false

        val user = usersClient.findUser(token) ?: return false

        return ADMIN_ROLE == user.role || user.id == submission.userId
    }
}
