package com.finlit.service.impl

import com.finlit.model.dto.SavingGoalDto
import com.finlit.model.entity.SavingGoal
import com.finlit.model.entity.StrategyType
import com.finlit.repository.SavingGoalRepository
import com.finlit.repository.UserRepository
import com.finlit.service.SavingGoalService
import com.finlit.service.SavingStrategy
import com.finlit.service.strategy.AcceleratedStrategy
import com.finlit.service.strategy.FixedAmountStrategy
import com.finlit.service.strategy.PercentageStrategy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
@Transactional
class SavingGoalServiceImpl(
    private val savingGoalRepository: SavingGoalRepository,
    private val userRepository: UserRepository,
    private val fixedStrategy: FixedAmountStrategy,
    private val percentageStrategy: PercentageStrategy,
    private val acceleratedStrategy: AcceleratedStrategy
) : SavingGoalService {

    private fun getStrategy(type: StrategyType): SavingStrategy {
        return when (type) {
            StrategyType.FIXED       -> fixedStrategy
            StrategyType.PERCENTAGE  -> percentageStrategy
            StrategyType.ACCELERATED -> acceleratedStrategy
            else                     -> fixedStrategy  // ← fix: tambah else
        }
    }

    override fun create(dto: SavingGoalDto, userEmail: String): SavingGoal {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User tidak ditemukan") }

        val goal = SavingGoal(
            name          = dto.name,
            targetAmount  = dto.targetAmount!!,
            currentAmount = dto.currentAmount ?: BigDecimal.ZERO,
            deadline      = dto.deadline!!,
            strategyType  = dto.strategyType ?: StrategyType.FIXED,
            user          = user
        )
        return savingGoalRepository.save(goal)
    }

    @Transactional(readOnly = true)
    override fun findByUser(userEmail: String): List<SavingGoal> {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User tidak ditemukan") }
        return savingGoalRepository.findByUserIdOrderByDeadlineAsc(user.id!!)
    }

    override fun delete(id: Long) {
        savingGoalRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun hitungSetoran(goalId: Long, strategyType: StrategyType): BigDecimal {
        val goal = savingGoalRepository.findById(goalId)
            .orElseThrow { RuntimeException("Goal tidak ditemukan") }
        val sisaBulan = ChronoUnit.MONTHS.between(LocalDate.now(), goal.deadline)
        val strategy = getStrategy(strategyType)
        return strategy.hitungSetoran(goal.targetAmount, goal.currentAmount, sisaBulan)
    }
}