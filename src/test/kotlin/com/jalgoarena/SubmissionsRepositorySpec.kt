package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.data.XodusSubmissionsRepository
import com.jalgoarena.domain.Submission
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class SubmissionsRepositorySpec {

    companion object {

        val testDbName = "./SubmissionsStoreForTest"
        val repository: SubmissionsRepository
        init {
            PersistentEntityStores.newInstance(testDbName).close()
            repository = XodusSubmissionsRepository(testDbName)
        }

        @AfterClass
        @JvmStatic fun tearDown() {
            repository.destroy()
            File(testDbName).deleteRecursively()
        }

    }

    @Test
    fun adds_new_submission() {
        val submission = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        assertThat(submission.id).isNotNull()
    }

    @Test
    fun updates_submission_if_userId_and_problemId_are_same() {
        val submissionKotlin = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        val submissionJava = repository.addOrUpdate(user1SubmissionForFibInJava)

        assertThat(submissionJava.id).isEqualTo(submissionKotlin.id)
    }

    @Test
    fun update_values_for_new_submission_with_same_problemId_and_userId() {
        val submissionKotlin = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        repository.addOrUpdate(user1SubmissionForFibInJava)

        val submission = repository.findById(submissionKotlin.id!!)
        assertThat(submission!!.language).isEqualTo("java")
    }

    @Test
    fun deletes_already_added_submission() {
        val submission = repository.addOrUpdate(user2SubmissionForFibInKotlin)
        repository.delete(submission.id!!)
        val deletedSubmission = repository.findById(submission.id!!)

        assertThat(deletedSubmission).isNull()
    }

    @Test
    fun returns_all_added_submissions() {
        val submission1 = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = repository.addOrUpdate(user2SubmissionForFibInKotlin)

        val allSubmissions = repository.findAll()
        assertThat(allSubmissions).isEqualTo(listOf(
                submission1,
                submission2
        ))
    }

    @Test
    fun returns_only_user_owned_submissions() {
        val submission1 = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        repository.addOrUpdate(user2SubmissionForFibInKotlin)

        val user1Submissions = repository.findByUserId(user1)
        assertThat(user1Submissions).isEqualTo(listOf(
                submission1
        ))
    }

    @Test
    fun finds_all_submissions_for_given_problem_id() {
        val submission1 = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        val submission2 = repository.addOrUpdate(user2SubmissionForFibInKotlin)
        repository.addOrUpdate(user2SubmissionForTwoSumInKotlin)

        val fibSubmissions = repository.findByProblemId("fib")
        assertThat(fibSubmissions).isEqualTo(listOf(
                submission1,
                submission2
        ))
    }

    @Test
    fun returns_empty_list_if_no_submissions_with_a_given_problem_id() {
        val submissions = repository.findByProblemId("non-existing")
        assertThat(submissions).isEmpty()
    }

    private fun submission(problemId: String, userId: String, language: String) =
            Submission(problemId, 1, 0.5, "class Solution", "ACCEPTED", userId, language)

    private val user1 = "User#1"
    private val user2 = "User#2"

    private val user1SubmissionForFibInKotlin = submission("fib", user1, "kotlin")
    private val user1SubmissionForFibInJava = submission("fib", user1, "java")
    private val user2SubmissionForFibInKotlin = submission("fib", user2, "kotlin")
    private val user2SubmissionForTwoSumInKotlin = submission("2-sum", user2, "kotlin")
}
