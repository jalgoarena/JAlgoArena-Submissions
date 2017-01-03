package com.jalgoarena.web

import com.jalgoarena.domain.RankingCalculator
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@CrossOrigin
@RestController
class RankingController(
        @Inject val rankingCalculator: RankingCalculator,
        @Inject val usersClient: UsersClient
) {

    @GetMapping("/ranking", produces = arrayOf("application/json"))
    fun ranking() = rankingCalculator.ranking(usersClient.users())

    @GetMapping("/ranking/{problemId}", produces = arrayOf("application/json"))
    fun problemRanking(@PathVariable problemId: String) =
            rankingCalculator.problemRanking(problemId, usersClient.users())
}
