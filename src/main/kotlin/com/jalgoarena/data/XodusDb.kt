package com.jalgoarena.data

import com.jalgoarena.domain.Constants
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.springframework.stereotype.Component

@Component
class XodusDb(dbName: String): Db {

    override val store: PersistentEntityStore = PersistentEntityStores.newInstance(dbName)

    constructor() : this(Constants.storePathSubmissions)
}