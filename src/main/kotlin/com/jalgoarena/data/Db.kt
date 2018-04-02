package com.jalgoarena.data

import jetbrains.exodus.entitystore.PersistentEntityStore

interface Db {
    val store: PersistentEntityStore
}
