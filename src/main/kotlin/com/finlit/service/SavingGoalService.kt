package com.finlit.service

import com.finlit.model.dto.SavingGoalDto
import com.finlit.model.entity.SavingGoal
import com.finlit.model.entity.StrategyType
import java.math.BigDecimal

interface SavingGoalService {
    fun create(dto: SavingGoalDto, userEmail: String): SavingGoal
    fun findByUser(userEmail: String): List<SavingGoal>
    fun delete(id: Long)
    fun hitungSetoran(goalId: Long, strategyType: StrategyType): BigDecimal
}