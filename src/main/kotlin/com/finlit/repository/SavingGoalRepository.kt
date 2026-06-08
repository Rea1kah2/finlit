package com.finlit.repository

import com.finlit.model.entity.SavingGoal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

@Repository
interface SavingGoalRepository : JpaRepository<SavingGoal, Long> {
    fun findByUserIdOrderByDeadlineAsc(userId: Long): List<SavingGoal>

    @Query("""
        SELECT g FROM SavingGoal g
        WHERE g.user.id = :userId
        AND g.currentAmount < g.targetAmount
        AND g.deadline BETWEEN :today AND :limitDate
        ORDER BY g.deadline ASC
    """)
    fun findUpcomingDeadlines(
        @Param("userId") userId: Long,
        @Param("today") today: LocalDate,
        @Param("limitDate") limitDate: LocalDate
    ): List<SavingGoal>
}