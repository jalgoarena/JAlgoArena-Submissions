package com.jalgoarena.web

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.Submission
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@CrossOrigin
@RestController
class SubmissionsController(@Inject val repository: SubmissionsRepository) {

    @GetMapping("/submissions", produces = arrayOf("application/json"))
    fun submissions(): List<Submission> = repository.findAll()

    @GetMapping("/submissions/{userId}", produces = arrayOf("application/json"))
    fun submissions(@PathVariable userId: String) = repository.findAllByUserId(userId)

    @PostMapping("/submissions", produces = arrayOf("application/json"))
    fun addOrUpdateSubmission(@RequestBody submission: Submission) = repository.addOrUpdate(submission)

    @PostMapping("/submissions/delete/{submissionId}", produces = arrayOf("application/json"))
    fun deleteSubmission(@PathVariable submissionId: String) = repository.delete(submissionId)
}
