package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.SubmissionResult
import jetbrains.exodus.entitystore.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class XodusSubmissionResultsRepository(db: Db) : SubmissionResultsRepository {
    private val store = db.store

    private val logger = LoggerFactory.getLogger(this.javaClass)
    override fun findAll(): List<SubmissionResult> {
        return readonly {
            it.getAll(Constants.SUBMISSION_RESULT_ENTITY_TYPE).map { SubmissionResult.from(it) }
        }
    }

    override fun findByUserId(userId: String): List<SubmissionResult> {
        return readonly {
            it.find(
                    Constants.SUBMISSION_RESULT_ENTITY_TYPE,
                    Constants.userId,
                    userId
            ).map { SubmissionResult.from(it) }
        }
    }

    override fun findBySubmissionId(submissionId: String): SubmissionResult {
        return readonly {
            val submissionResult = it.find(
                    Constants.SUBMISSION_RESULT_ENTITY_TYPE,
                    Constants.submissionId,
                    submissionId
            ).firstOrNull()

            if (submissionResult == null) {
                SubmissionResult.notFound(submissionId)
            } else {
                SubmissionResult.from(submissionResult)
            }
        }
    }

    override fun findById(id: String): SubmissionResult? {
        return try {
            readonly {
                val entityId = PersistentEntityId.toEntityId(id)
                SubmissionResult.from(it.getEntity(entityId))
            }
        } catch(e: EntityRemovedInDatabaseException) {
            null
        }
    }

    override fun findByProblemId(problemId: String): List<SubmissionResult> {
        return readonly {
            it.find(
                    Constants.SUBMISSION_RESULT_ENTITY_TYPE,
                    Constants.problemId,
                    problemId
            ).map { SubmissionResult.from(it) }
        }
    }

    override fun addOrUpdate(submissionResult: SubmissionResult): SubmissionResult {
        return transactional {

            val existingEntity = it.find(Constants.SUBMISSION_RESULT_ENTITY_TYPE, Constants.userId, submissionResult.userId).intersect(
                    it.find(Constants.SUBMISSION_RESULT_ENTITY_TYPE, Constants.problemId, submissionResult.problemId)
            ).firstOrNull()

            val entity = when (existingEntity) {
                null -> it.newEntity(Constants.SUBMISSION_RESULT_ENTITY_TYPE)
                else -> existingEntity
            }

            updateEntity(entity, submissionResult)
        }
    }

    private fun updateEntity(entity: Entity, submissionResult: SubmissionResult): SubmissionResult {
        entity.apply {
            setProperty(Constants.problemId, submissionResult.problemId)
            setProperty(Constants.elapsedTime, submissionResult.elapsedTime)
            setProperty(Constants.sourceCode, submissionResult.sourceCode)
            setProperty(Constants.statusCode, submissionResult.statusCode)
            setProperty(Constants.userId, submissionResult.userId)
            setProperty(Constants.language, submissionResult.language)
            setProperty(Constants.submissionId, submissionResult.submissionId)
            setProperty(Constants.consumedMemory, submissionResult.consumedMemory)
            setProperty(Constants.errorMessage, submissionResult.errorMessage ?: "")
            setProperty(Constants.testcaseResults, submissionResult.testcaseResults.joinToString(";"))
        }

        return SubmissionResult.from(entity)
    }

    override fun delete(id: String): List<SubmissionResult> {
        val entityId = PersistentEntityId.toEntityId(id)

        transactional {
            val entity = it.getEntity(entityId)
            entity.delete()
        }

        return findAll()
    }

    override fun destroy() {
        var proceed = true
        var count = 1
        while (proceed && count <= 10) {
            try {
                logger.info("trying to close persistent store. attempt {}", count)
                store.close()
                proceed = false
                logger.info("persistent store closed")
            } catch (e: RuntimeException) {
                logger.error("error closing persistent store", e)
                count++
            }
        }
    }

    private fun <T> transactional(call: (PersistentStoreTransaction) -> T): T {
        return store.computeInTransaction { call(it as PersistentStoreTransaction) }
    }

    private fun <T> readonly(call: (PersistentStoreTransaction) -> T): T {
        return store.computeInReadonlyTransaction { call(it as PersistentStoreTransaction) }
    }
}
