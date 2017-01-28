package com.jalgoarena.web

import com.jalgoarena.ranking.RankingCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
class RankingController(
        @Inject private val rankingCalculator: RankingCalculator,
        @Inject private val usersClient: UsersClient
) {

    @GetMapping("/ranking", produces = arrayOf("application/json"))
    fun ranking() = rankingCalculator.ranking(usersClient.findAllUsers())

    @GetMapping("/ranking/{problemId}", produces = arrayOf("application/json"))
    fun problemRanking(@PathVariable problemId: String) =
            rankingCalculator.problemRanking(problemId, usersClient.findAllUsers())
}
