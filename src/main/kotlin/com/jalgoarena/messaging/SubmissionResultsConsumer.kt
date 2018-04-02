package com.jalgoarena.messaging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.data.SubmissionResultsRepository
import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Constants.ADMIN_ROLE
import com.jalgoarena.domain.SubmissionResult
import com.jalgoarena.web.UsersClient
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
@KafkaListener(topics = ["results"])
class SubmissionResultsConsumer(
        @Inject private val submissionResultsRepository: SubmissionResultsRepository,
        @Inject private val submissionsRepository: SubmissionsRepository,
        @Inject private val usersClient: UsersClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaHandler
    fun store(message: String) {

        val submissionResult: SubmissionResult = jacksonObjectMapper().readValue(message, SubmissionResult::class.java)

        logger.info("Received submissionResult result [submissionId={}]", submissionResult.submissionId)

        if (isValidUser(submissionResult)) {
            submissionResultsRepository.addOrUpdate(submissionResult)
            logger.info("SubmissionResult result is saved [submissionId={}]", submissionResult.submissionId)

            val submissionInProgress = submissionsRepository.findByUserId(submissionResult.userId).firstOrNull { submissionInProgress ->
                submissionInProgress.submissionId == submissionResult.submissionId
            }

            if (submissionInProgress == null) {
                logger.info("Skipping state update for submissionResult - cannot find it [submissionId={}]", submissionResult.submissionId)
                return
            }

            submissionInProgress.state = Constants.DONE
            submissionsRepository.addOrUpdate(submissionInProgress)
            logger.info("SubmissionResult marked as done [submissionId={}]", submissionInProgress.submissionId)

        } else {
            logger.warn(
                    "Cannot store submissionResult [submissionId={}] - authentication failed",
                    submissionResult.submissionId,
                    submissionResult.userId
            )
        }
    }

    private fun isValidUser(submissionResult: SubmissionResult): Boolean {
        val token = submissionResult.token

        if (token == null) {
            logger.warn("Token is empty!")
            return false
        }

        val user = usersClient.findUser(token)

        if (user == null) {
            logger.warn("No user with id: {}", submissionResult.userId)
            return false
        }

        val result = ADMIN_ROLE == user.role || user.id == submissionResult.userId

        return if (result) {
            true
        } else {
            logger.warn("Your are not ADMIN nor the authenticated user is not an owner of submissionResult")
            false
        }
    }
}
