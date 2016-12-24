package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import com.winterbe.expekt.should
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.junit.AfterClass
import org.junit.Test
import java.io.File

class SubmissionsRepositorySpec {

    val dummySubmission = { language: String -> Submission("fib", 1, 0.5, "class Solution", "ACCEPTED", "SDDAD8897", language) }

    companion object {
        val testDbName = "./SubmissionsStoreForTest"
        val repository: SubmissionsRepository

        init {
            PersistentEntityStores.newInstance(testDbName).close()
            repository = SubmissionsRepository(testDbName)
        }

        @AfterClass
        @JvmStatic fun tearDown() {
            repository.destroy()
            File(testDbName).deleteRecursively()
        }
    }

    @Test
    fun should_allow_on_adding_new_submission() {
        val submission = repository.addOrUpdate(dummySubmission("kotlin"))
        submission.id.should.not.be.empty
    }

    @Test
    fun should_update_submission_if_userId_and_problemId_are_same() {
        val submissionKotlin = repository.addOrUpdate(dummySubmission("kotlin"))
        val submissionJava = repository.addOrUpdate(dummySubmission("java"))

        submissionJava.id.should.equal(submissionKotlin.id)
    }

    @Test
    fun should_update_values_for_new_submission_with_same_problemId_and_userId() {
        val submissionKotlin = repository.addOrUpdate(dummySubmission("kotlin"))
        repository.addOrUpdate(dummySubmission("java"))

        val submission = repository.find(submissionKotlin.id!!)
        submission!!.language.should.equal("java")
    }
}
