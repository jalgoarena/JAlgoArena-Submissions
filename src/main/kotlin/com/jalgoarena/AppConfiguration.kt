package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.ranking.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfiguration {

    @Bean
    open fun rankingCalculator(submissionsRepository : SubmissionsRepository): RankingCalculator {
        val scoreCalculator = KotlinBonusScoreCalculator(BasicScoreCalculator())
        val rankingCalculator = BasicRankingCalculator(submissionsRepository, scoreCalculator)

        return BonusPointsForBestTimeRankingCalculator(
                submissionsRepository, rankingCalculator
        )
    }
}
