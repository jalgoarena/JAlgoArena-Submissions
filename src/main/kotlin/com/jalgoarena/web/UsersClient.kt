package com.jalgoarena.web

import com.jalgoarena.domain.User
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.ws.rs.WebApplicationException

@FeignClient("jalgoarena-auth", fallback = HystrixUsersClientFallback::class)
interface UsersClient {
    @RequestMapping("/users", method = arrayOf(RequestMethod.GET),  produces = arrayOf("application/json"))
    fun users(): List<User>

    @RequestMapping("/api/user", method = arrayOf(RequestMethod.GET), produces = arrayOf("application/json"))
    fun user(@RequestHeader("X-Authorization") token: String): User
}

class HystrixUsersClientFallback : UsersClient {
    override fun users(): List<User> = throw WebApplicationException(500)
    override fun user(token: String) = throw WebApplicationException(500)
}
