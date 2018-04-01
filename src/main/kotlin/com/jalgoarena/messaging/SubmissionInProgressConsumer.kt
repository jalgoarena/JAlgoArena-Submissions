package com.jalgoarena.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsInProgressRepository
import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Constants.ADMIN_ROLE
import com.jalgoarena.domain.SubmissionInProgress
import com.jalgoarena.web.UsersClient
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
@KafkaListener(topics = ["submissions"])
class SubmissionInProgressConsumer(
        @Inject private val submissionsInProgressRepository: SubmissionsInProgressRepository,
        @Inject private val usersClient: UsersClient
        ) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaHandler
    fun store(message: String) {

        val submissionInProgress = jacksonObjectMapper().readValue(message, SubmissionInProgress::class.java)

        if (isValidUser(submissionInProgress)) {
            submissionInProgress.state = Constants.IN_PROGRESS
            submissionsInProgressRepository.addOrUpdate(submissionInProgress)
        } else {
            logger.warn(
                    "Trying to store submission [submissionId={}] in progress for invalid user [userId={}]",
                    submissionInProgress.submissionId,
                    submissionInProgress.userId
            )
        }
    }

    private fun isValidUser(submissionInProgress: SubmissionInProgress): Boolean {
        val token = submissionInProgress.token ?: return false

        val user = usersClient.findUser(token) ?: return false

        return ADMIN_ROLE == user.role || user.id == submissionInProgress.userId
    }
}
