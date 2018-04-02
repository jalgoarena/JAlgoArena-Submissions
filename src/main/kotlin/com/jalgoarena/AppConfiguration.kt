package com.jalgoarena

import com.jalgoarena.data.SubmissionResultsRepository
import com.jalgoarena.ranking.*
import com.jalgoarena.web.ProblemsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

@Configuration
open class AppConfiguration {

    @Bean
    open fun rankingCalculator(submissionResultsRepository : SubmissionResultsRepository, problemsClient: ProblemsClient): RankingCalculator {
        val scoreCalculator = BasicScoreCalculator()
        val rankingCalculator = BasicRankingCalculator(submissionResultsRepository, scoreCalculator)

        return BonusPointsForBestTimeRankingCalculator(
                submissionResultsRepository, rankingCalculator
        )
    }

    @Bean
    open fun restTemplate(): RestOperations = RestTemplate()
}
