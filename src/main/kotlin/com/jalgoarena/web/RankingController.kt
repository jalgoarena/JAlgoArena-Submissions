package com.jalgoarena.web

import com.jalgoarena.domain.RankingCalculator
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@CrossOrigin
@RestController
class RankingController(@Inject val rankingCalculator: RankingCalculator, @Inject val usersClient: UsersClient) {

    @GetMapping("/ranking", produces = arrayOf("application/json"))
    fun ranking() = rankingCalculator.ranking(usersClient.findAllUsers())

    @GetMapping("/ranking/{problemId}", produces = arrayOf("application/json"))
    fun problemRanking(@PathVariable problemId: String) =
            rankingCalculator.problemRanking(problemId, usersClient.findAllUsers())
}
