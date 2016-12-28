package com.jalgoarena.web

import com.jalgoarena.domain.ProblemRankEntry
import com.jalgoarena.domain.RankEntry
import com.jalgoarena.domain.RankingCalculator
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@CrossOrigin
@RestController
class RankingController(
        @Inject val rankingCalculator: RankingCalculator,
        @Inject val usersClient: UsersClient
) {
    @GetMapping("/ranking", produces = arrayOf("application/json"))
    fun ranking(@RequestHeader("X-Authorization") token: String): List<RankEntry> {
        val users = usersClient.findAllUsers(token)
        return rankingCalculator.ranking(users)
    }

    @GetMapping("/ranking/{problemId}", produces = arrayOf("application/json"))
    fun problemRanking(
            @PathVariable problemId: String,
            @RequestHeader("X-Authorization") token: String
    ): List<ProblemRankEntry> {
        val users = usersClient.findAllUsers(token)
        return rankingCalculator.problemRanking(problemId, users)
    }
}
