package com.jalgoarena

import com.jalgoarena.data.SubmissionsRepository
import com.jalgoarena.domain.BasicRankingCalculator
import com.jalgoarena.domain.BonusPointsForBestTimeRankingCalculator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfiguration {

    @Bean
    open fun rankingCalculator(submissionsRepository : SubmissionsRepository) =
            BonusPointsForBestTimeRankingCalculator(submissionsRepository, BasicRankingCalculator(submissionsRepository))
}
