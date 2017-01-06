package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import com.jalgoarena.domain.Submission
import jetbrains.exodus.entitystore.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import javax.annotation.PreDestroy

@Repository
class XodusSubmissionsRepository(dbName: String) : SubmissionsRepository {

    constructor() : this(Constants.storePath)

    private val LOG = LoggerFactory.getLogger(this.javaClass)
    private val store: PersistentEntityStore = PersistentEntityStores.newInstance(dbName)

    override fun findAll(): List<Submission> {
        return readonly {
            it.getAll(Constants.entityType).map { Submission.from(it) }
        }
    }

    override fun findByUserId(userId: String): List<Submission> {
        return readonly {
            it.find(
                    Constants.entityType,
                    Constants.userId,
                    userId
            ).map { Submission.from(it) }
        }
    }

    override fun findById(id: String): Submission? {
        return try {
            readonly {
                val entityId = PersistentEntityId.toEntityId(id)
                Submission.from(it.getEntity(entityId))
            }
        } catch(e: EntityRemovedInDatabaseException) {
            null
        }
    }

    override fun findByProblemId(problemId: String): List<Submission> {
        return readonly {
            it.find(
                    Constants.entityType,
                    Constants.problemId,
                    problemId
            ).map { Submission.from(it) }
        }
    }

    override fun addOrUpdate(submission: Submission): Submission {
        return transactional {

            val existingEntity = it.find(Constants.entityType, Constants.userId, submission.userId).intersect(
                    it.find(Constants.entityType, Constants.problemId, submission.problemId)
            ).firstOrNull()

            val entity = when (existingEntity) {
                null -> it.newEntity(Constants.entityType)
                else -> existingEntity
            }

            updateEntity(entity, submission)
        }
    }

    private fun updateEntity(entity: Entity, submission: Submission): Submission {
        entity.apply {
            setProperty(Constants.problemId, submission.problemId)
            setProperty(Constants.level, submission.level)
            setProperty(Constants.elapsedTime, submission.elapsedTime)
            setProperty(Constants.sourceCode, submission.sourceCode)
            setProperty(Constants.statusCode, submission.statusCode)
            setProperty(Constants.userId, submission.userId)
            setProperty(Constants.language, submission.language)
        }

        return Submission.from(entity)
    }

    override fun delete(id: String) {
        val entityId = PersistentEntityId.toEntityId(id)

        return transactional {
            val entity = it.getEntity(entityId)
            entity.delete()
        }
    }

    @PreDestroy
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
        return transactional(store, call)
    }

    private fun <T> readonly(call: (PersistentStoreTransaction) -> T): T {
        return readonly(store, call)
    }
}

fun <T> transactional(store: PersistentEntityStore, call: (PersistentStoreTransaction) -> T): T {
    return store.computeInTransaction { call(it as PersistentStoreTransaction) }
}

fun <T> readonly(store: PersistentEntityStore, call: (PersistentStoreTransaction) -> T): T {
    return store.computeInReadonlyTransaction { call(it as PersistentStoreTransaction) }
}
