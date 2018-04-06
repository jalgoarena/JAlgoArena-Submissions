package com.jalgoarena.data

import com.jalgoarena.domain.Submission
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Test
import java.io.File
import java.time.LocalDateTime

class SubmissionsRepositorySpec {

    companion object {

        private const val TEST_DB_NAME = "./SubmissionsStoreForTest"
        val REPOSITORY: SubmissionsRepository
        init {
            PersistentEntityStores.newInstance(TEST_DB_NAME).close()
            REPOSITORY = XodusSubmissionsRepository(XodusDb(TEST_DB_NAME))
        }

        @AfterClass
        @JvmStatic fun tearDown() {
            REPOSITORY.destroy()
            File(TEST_DB_NAME).deleteRecursively()
        }

    }

    @Test
    fun adds_new_submission() {
        val submission = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)

        try {
            assertThat(submission.id).isNotNull()
        } finally {
            REPOSITORY.delete(submission.id!!)
        }
    }

    @Test
    fun deletes_already_added_submission() {
        val submission = REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)

        REPOSITORY.delete(submission.id!!)
        val deletedSubmission = REPOSITORY.findById(submission.id!!)

        assertThat(deletedSubmission).isNull()
    }

    @Test
    fun returns_all_added_submissions() {
        val submission1 = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)

        try {
            val allSubmissions = REPOSITORY.findAll()

            assertThat(allSubmissions).isEqualTo(listOf(
                    submission1,
                    submission2
            ))
        } finally {
            REPOSITORY.delete(submission1.id!!)
            REPOSITORY.delete(submission2.id!!)
        }
    }

    @Test
    fun returns_only_user_owned_submissions() {
        val submission1 = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)
        try {
            val user1Submissions = REPOSITORY.findByUserId(user1)

            assertThat(user1Submissions).isEqualTo(listOf(
                    submission1
            ))
        } finally {
            REPOSITORY.delete(submission1.id!!)
            REPOSITORY.delete(submission2.id!!)
        }
    }

    @Test
    fun finds_all_submissions_for_given_problem_id() {
        val submission1 = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)
        val submission3 = REPOSITORY.addOrUpdate(user2SubmissionForTwoSumInKotlin)

        try {
            val fibSubmissions = REPOSITORY.findByProblemId("fib")
            assertThat(fibSubmissions).isEqualTo(listOf(
                    submission1,
                    submission2
            ))
        } finally {
            REPOSITORY.delete(submission1.id!!)
            REPOSITORY.delete(submission2.id!!)
            REPOSITORY.delete(submission3.id!!)
        }
    }

    @Test
    fun finds_all_submissions() {
        val submission1 = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)
        val submission3 = REPOSITORY.addOrUpdate(user2SubmissionForTwoSumInKotlin)
        val submission4 = REPOSITORY.addOrUpdate(user1SubmissionForFibInJava)

        try {
            val fibSubmissions = REPOSITORY.findAll()
            assertThat(fibSubmissions).isEqualTo(listOf(
                    submission1,
                    submission2,
                    submission3,
                    submission4
            ))
        } finally {
            REPOSITORY.delete(submission1.id!!)
            REPOSITORY.delete(submission2.id!!)
            REPOSITORY.delete(submission3.id!!)
            REPOSITORY.delete(submission4.id!!)
        }
    }

    @Test
    fun returns_empty_list_if_no_submissions_with_a_given_problem_id() {
        val submissions = REPOSITORY.findByProblemId("non-existing")
        assertThat(submissions).isEmpty()
    }

    private fun submission(problemId: String, userId: String, language: String, submissionId: String) =
            Submission(
                    problemId,
                    "class Solution",
                    "ACCEPTED",
                    userId,
                    language,
                    submissionId,
                    LocalDateTime.now().toString(),
                    0.5,
                    10L,
                    null,
                    1,
                    0
            )

    private val user1 = "User#1"
    private val user2 = "User#2"

    private val user1SubmissionForFibInKotlin = submission("fib", user1, "kotlin", "1")
    private val user1SubmissionForFibInJava = submission("fib", user1, "java", "2")
    private val user2SubmissionForFibInKotlin = submission("fib", user2, "kotlin", "3")
    private val user2SubmissionForTwoSumInKotlin = submission("2-sum", user2, "kotlin", "4")
}
