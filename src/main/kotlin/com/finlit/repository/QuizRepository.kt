package com.finlit.repository

import com.finlit.model.entity.QuizQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : JpaRepository<QuizQuestion, Long> {
    fun findByCategory(category: String): List<QuizQuestion>
}