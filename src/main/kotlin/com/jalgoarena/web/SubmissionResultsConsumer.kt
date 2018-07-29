package com.jalgoarena.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.RankingEvent
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
class SubmissionResultsConsumer(
        @Autowired private val submissionsRepository: SubmissionsRepository,
        @Autowired private val usersClient: UsersClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userSubmissionsEventPublisher: KafkaTemplate<Int, UserSubmissionsEvent>

    @Autowired
    private lateinit var rankingEventPublisher: KafkaTemplate<Int, RankingEvent>

    @KafkaListener(topics = ["results"])
    fun storeSubmission(message: String) {

        try {
            val submission = toSubmission(message)

            logger.info("Received submission result [submissionId={}][status={}]",
                    submission.submissionId,
                    submission.statusCode)

            if (isValidUser(submission)) {
                submissionsRepository.save(submission)
                logger.info("Submission result is saved [submissionId={}]", submission.submissionId)

                val usersSubmissionsEventFuture = userSubmissionsEventPublisher.send(
                        "events", UserSubmissionsEvent(userId = submission.userId)
                )
                usersSubmissionsEventFuture.addCallback(
                        UserSubmissionsEventPublishHandler(submission.submissionId, "submission result")
                )

                val rankingEventFuture = rankingEventPublisher.send(
                        "events", RankingEvent(problemId = submission.problemId)
                )
                rankingEventFuture.addCallback(RankingEventPublishHandler(submission.submissionId))

            } else {
                logger.warn(
                        "Cannot store Submission result [submissionId={}] - authentication failed",
                        submission.submissionId,
                        submission.userId
                )
            }
        } catch (ex: Exception) {
            logger.error("Cannot store submission result: {}", message, ex)
            throw ex
        }
    }

    private fun toSubmission(message: String): Submission {
        return jacksonObjectMapper().readValue<Submission>(message, Submission::class.java)
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

    class UserSubmissionsEventPublishHandler(
            private val submissionId: String, private val submissionType: String
    ) : ListenableFutureCallback<SendResult<Int, UserSubmissionsEvent>> {

        private val logger = LoggerFactory.getLogger(this.javaClass)

        override fun onSuccess(result: SendResult<Int, UserSubmissionsEvent>?) {
            logger.info("Requested user submissions refresh for {} [submissionId={}]",
                    submissionType, submissionId)
        }

        override fun onFailure(ex: Throwable?) {
            logger.error("Error during user submissions refresh for submission [submissionId={}]",
                    submissionId, ex)
        }
    }

    class RankingEventPublishHandler(
            private val submissionId: String
    ) : ListenableFutureCallback<SendResult<Int, RankingEvent>> {

        private val logger = LoggerFactory.getLogger(this.javaClass)

        override fun onSuccess(result: SendResult<Int, RankingEvent>?) {
            logger.info("Requested ranking refresh after new submission [submissionId={}]", submissionId)
        }

        override fun onFailure(ex: Throwable?) {
            logger.error("Error during ranking refresh for submission [submissionId={}]", submissionId, ex)
        }

    }
}
