package com.jalgoarena.web

import com.jalgoarena.domain.User

interface UsersClient {
    fun findAllUsers(): Array<User>
    fun findUser(token: String): User?
}
