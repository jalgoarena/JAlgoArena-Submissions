package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@CrossOrigin
@RestController
class SubmissionsController(
        @Inject val repository: SubmissionsRepository,
        @Inject val usersClient: UsersClient,
        @Inject val validation: UserPermissionValidation) {

    @GetMapping("/submissions", produces = arrayOf("application/json"))
    fun submissions(@RequestHeader("X-Authorization") token: String): List<Submission> {
        val user = usersClient.findUser(token)
        validation.checkForAdmin(user)
        return repository.findAll()
    }

    @GetMapping("/submissions/{userId}", produces = arrayOf("application/json"))
    fun submissions(@PathVariable userId: String, @RequestHeader("X-Authorization") token: String): List<Submission> {
        val user = usersClient.findUser(token)
        validation.confirmUserId(user, userId)
        return repository.findByUserId(userId)
    }

    @PostMapping("/submissions", produces = arrayOf("application/json"))
    fun addOrUpdateSubmission(@RequestBody submission: Submission, @RequestHeader("X-Authorization") token: String): Submission {
        val user = usersClient.findUser(token)
        validation.confirmUserId(user, submission.userId)
        return repository.addOrUpdate(submission)
    }

    @PostMapping("/submissions/delete/{submissionId}", produces = arrayOf("application/json"))
    fun deleteSubmission(@PathVariable submissionId: String, @RequestHeader("X-Authorization") token: String) {
        val user = usersClient.findUser(token)
        validation.checkForAdmin(user)
        repository.delete(submissionId)
    }
}
