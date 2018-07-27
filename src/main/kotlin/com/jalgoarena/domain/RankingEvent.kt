package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RankingEvent(
        val type: String = "refreshRanking",
        val problemId: String
)