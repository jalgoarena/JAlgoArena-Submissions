package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.data.XodusSubmissionsRepository
import com.jalgoarena.domain.Submission
import com.winterbe.expekt.should
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class SubmissionsRepositorySpec {

    val dummySubmission = { problemId: String, userId: String, language: String -> Submission(problemId, 1, 0.5, "class Solution", "ACCEPTED", userId, language) }

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
    fun should_allow_on_adding_new_submission() {
        val submission = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        submission.id.should.not.be.`null`
    }

    @Test
    fun should_update_submission_if_userId_and_problemId_are_same() {
        val submissionKotlin = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        val submissionJava = repository.addOrUpdate(user1SubmissionForFibInJava)

        submissionJava.id.should.equal(submissionKotlin.id)
    }

    @Test
    fun should_update_values_for_new_submission_with_same_problemId_and_userId() {
        val submissionKotlin = repository.addOrUpdate(user1SubmissionForFibInKotlin)
        repository.addOrUpdate(user1SubmissionForFibInJava)

        val submission = repository.findById(submissionKotlin.id!!)
        submission!!.language.should.equal("java")
    }

    @Test
    fun should_delete_already_added_submission() {
        val submission = repository.addOrUpdate(user2SubmissionForFibInKotlin)
        repository.delete(submission.id!!)
        val deletedSubmission = repository.findById(submission.id!!)

        deletedSubmission.should.be.`null`
    }

    @Test
    fun should_return_all_added_submissions() {
        repository.addOrUpdate(user1SubmissionForFibInKotlin)
        repository.addOrUpdate(user2SubmissionForFibInKotlin)

        val allSubmissions = repository.findAll()
        allSubmissions.should.have.size(2)
    }

    @Test
    fun should_return_only_specific_to_user_submissions() {
        repository.addOrUpdate(user1SubmissionForFibInKotlin)
        repository.addOrUpdate(user2SubmissionForFibInKotlin)

        val user1Submissions = repository.findByUserId(user1)
        user1Submissions.should.have.size(1)
    }

    private val user1 = "User#1"
    private val user2 = "User#2"
    private val user1SubmissionForFibInKotlin = dummySubmission("fib", user1, "kotlin")
    private val user1SubmissionForFibInJava = dummySubmission("fib", user1, "java")
    private val user2SubmissionForFibInKotlin = dummySubmission("fib", user2, "kotlin")
}
