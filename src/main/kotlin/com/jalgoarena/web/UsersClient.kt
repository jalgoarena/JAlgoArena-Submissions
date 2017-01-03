package com.jalgoarena.web

import com.jalgoarena.domain.User
import feign.Headers
import feign.Param
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient("jalgoarena-auth")
interface UsersClient {
    @RequestMapping("/users", method = arrayOf(RequestMethod.GET),  produces = arrayOf("application/json"))
    fun findAllUsers(): List<User>

    @RequestMapping("/api/user", method = arrayOf(RequestMethod.GET), produces = arrayOf("application/json"))
    @Headers("X-Authorization: {token}")
    fun findUser(@Param("token") token: String): User
}
