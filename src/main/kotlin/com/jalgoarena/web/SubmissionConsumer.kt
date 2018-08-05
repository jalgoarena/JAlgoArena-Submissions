package com.jalgoarena.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.UserSubmissionsEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class SubmissionConsumer(
        @Autowired private val submissionsRepository: SubmissionsRepository,
        @Autowired private val usersClient: UsersClient,
        @Autowired private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userSubmissionsEventPublisher: KafkaTemplate<Int, String>

    @Autowired
    private lateinit var submissionPublisher: KafkaTemplate<Int, String>

    @KafkaListener(topics = ["submissions"])
    fun storeSubmission(message: String) {

        try {
            val submission = toSubmission(message)

            logger.info("Received submission [submissionId={}][status={}]",
                    submission.submissionId,
                    submission.statusCode)

            if (isValidUser(submission)) {
                val savedSubmission = submissionsRepository.save(submission)
                logger.info(
                        "Submission is saved [submissionId={},id={}]",
                        savedSubmission.submissionId, savedSubmission.id
                )

                val submissionsToJudgeFuture = submissionPublisher.send(
                        "submissionsToJudge", objectMapper.writeValueAsString(savedSubmission)
                )
                submissionsToJudgeFuture.addCallback(SubmissionToJudgePublishHandler(
                        savedSubmission.submissionId, savedSubmission.id!!
                ))

                val future = userSubmissionsEventPublisher.send(
                        "events", objectMapper.writeValueAsString(
                        UserSubmissionsEvent(
                                userId = submission.userId,
                                problemId = submission.problemId,
                                submissionId = submission.submissionId
                        )
                )
                )
                future.addCallback(SubmissionResultsConsumer.UserSubmissionsEventPublishHandler(
                        submission.submissionId, "new submission"
                ))

            } else {
                logger.warn(
                        "Cannot store Submission [submissionId={}] - authentication failed",
                        submission.submissionId,
                        submission.userId
                )
            }
        } catch (ex: Exception) {
            logger.error("Cannot store submission: {}", message, ex)
            throw ex
        }
    }

    private fun toSubmission(message: String): Submission {
        return objectMapper.readValue<Submission>(message, Submission::class.java)
    }

    private fun isValidUser(submission: Submission): Boolean {
        val token = submission.token

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

    class SubmissionToJudgePublishHandler(
            private val submissionId: String, private val id: Int
    ) : ListenableFutureCallback<SendResult<Int, String>> {

        private val logger = LoggerFactory.getLogger(this.javaClass)

        override fun onSuccess(result: SendResult<Int, String>?) {
            logger.info("Submission passed for judgement [submissionId={}, id={}]",
                    submissionId, id
            )
        }

        override fun onFailure(ex: Throwable?) {
            logger.error("Error during passing submission for judgement [submissionId={}, id={}]",
                    submissionId, id, ex
            )
        }
    }
}
