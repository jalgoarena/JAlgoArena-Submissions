package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Submission
import com.jalgoarena.domain.SubmissionInProgress
import jetbrains.exodus.entitystore.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class XodusSubmissionsInProgressRepository(dbName: String) : SubmissionsInProgressRepository {

    constructor() : this(Constants.storePathSubmissionsInProgress)

    private val LOG = LoggerFactory.getLogger(this.javaClass)
    private val store: PersistentEntityStore = PersistentEntityStores.newInstance(dbName)

    override fun findAll(): List<SubmissionInProgress> {
        return readonly {
            it.getAll(Constants.entityType).map { SubmissionInProgress.from(it) }
        }
    }

    override fun findByUserId(userId: String): List<SubmissionInProgress> {
        return readonly {
            it.find(
                    Constants.entityType,
                    Constants.userId,
                    userId
            ).map { SubmissionInProgress.from(it) }
        }
    }

    override fun findById(id: String): SubmissionInProgress? {
        return try {
            readonly {
                val entityId = PersistentEntityId.toEntityId(id)
                SubmissionInProgress.from(it.getEntity(entityId))
            }
        } catch(e: EntityRemovedInDatabaseException) {
            null
        }
    }

    override fun findByProblemId(problemId: String): List<SubmissionInProgress> {
        return readonly {
            it.find(
                    Constants.entityType,
                    Constants.problemId,
                    problemId
            ).map { SubmissionInProgress.from(it) }
        }
    }

    override fun addOrUpdate(submissionInProgress: SubmissionInProgress): SubmissionInProgress {
        return transactional {

            val existingEntity = it.find(Constants.entityType, Constants.userId, submissionInProgress.userId).intersect(
                    it.find(Constants.entityType, Constants.problemId, submissionInProgress.problemId)
            ).firstOrNull()

            val entity = when (existingEntity) {
                null -> it.newEntity(Constants.entityType)
                else -> existingEntity
            }

            updateEntity(entity, submissionInProgress)
        }
    }

    private fun updateEntity(entity: Entity, submission: SubmissionInProgress): SubmissionInProgress {
        entity.apply {
            setProperty(Constants.sourceCode, submission.sourceCode)
            setProperty(Constants.userId, submission.userId)
            setProperty(Constants.language, submission.language)
            setProperty(Constants.submissionId, submission.submissionId)
            setProperty(Constants.problemId, submission.problemId)
        }

        return SubmissionInProgress.from(entity)
    }

    override fun delete(id: String): List<SubmissionInProgress> {
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
                LOG.info("trying to close persistent store. attempt {}", count)
                store.close()
                proceed = false
                LOG.info("persistent store closed")
            } catch (e: RuntimeException) {
                LOG.error("error closing persistent store", e)
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
