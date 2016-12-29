package com.jalgoarena.web

import com.jalgoarena.domain.User
import org.springframework.stereotype.Service

@Service
class UserPermissionValidation {
    fun checkForAdmin(user: User) {
        if ("ADMIN" != user.role) {
            throw PermissionException()
        }
    }
    fun confirmUserId(user: User, userId: String) {
        if (user.id != userId) {
            throw PermissionException()
        }
    }
}
