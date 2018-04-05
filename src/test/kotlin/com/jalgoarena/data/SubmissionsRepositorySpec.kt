package com.jalgoarena.data

import com.jalgoarena.domain.Submission
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Test
import java.io.File

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
        assertThat(submission.id).isNotNull()
    }

    @Test
    fun updates_submission_if_userId_and_problemId_are_same() {
        val submissionKotlin = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        val submissionJava = REPOSITORY.addOrUpdate(user1SubmissionForFibInJava)

        assertThat(submissionJava.id).isEqualTo(submissionKotlin.id)
    }

    @Test
    fun update_values_for_new_submission_with_same_problemId_and_userId() {
        val submissionKotlin = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        REPOSITORY.addOrUpdate(user1SubmissionForFibInJava)

        val submission = REPOSITORY.findById(submissionKotlin.id!!)
        assertThat(submission!!.language).isEqualTo("java")
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

        val allSubmissions = REPOSITORY.findAll()
        assertThat(allSubmissions).isEqualTo(listOf(
                submission1,
                submission2
        ))
    }

    @Test
    fun returns_only_user_owned_submissions() {
        val submission1 = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)

        val user1Submissions = REPOSITORY.findByUserId(user1)
        assertThat(user1Submissions).isEqualTo(listOf(
                submission1
        ))
    }

    @Test
    fun finds_all_submissions_for_given_problem_id() {
        val submission1 = REPOSITORY.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = REPOSITORY.addOrUpdate(user2SubmissionForFibInKotlin)
        REPOSITORY.addOrUpdate(user2SubmissionForTwoSumInKotlin)

        val fibSubmissions = REPOSITORY.findByProblemId("fib")
        assertThat(fibSubmissions).isEqualTo(listOf(
                submission1,
                submission2
        ))
    }

    @Test
    fun returns_empty_list_if_no_submissions_with_a_given_problem_id() {
        val submissions = REPOSITORY.findByProblemId("non-existing")
        assertThat(submissions).isEmpty()
    }

    private fun submission(problemId: String, userId: String, language: String) =
            Submission(
                    problemId,
                    "class Solution",
                    "ACCEPTED",
                    userId,
                    language,
                    "1",
                    0.5,
                    10L,
                    null,
                    emptyList()
            )

    private val user1 = "User#1"
    private val user2 = "User#2"

    private val user1SubmissionForFibInKotlin = submission("fib", user1, "kotlin")
    private val user1SubmissionForFibInJava = submission("fib", user1, "java")
    private val user2SubmissionForFibInKotlin = submission("fib", user2, "kotlin")
    private val user2SubmissionForTwoSumInKotlin = submission("2-sum", user2, "kotlin")
}
