package com.jalgoarena.data

import com.jalgoarena.domain.Problem

interface ProblemsRepository {
    fun findAll(): List<Problem>
}
