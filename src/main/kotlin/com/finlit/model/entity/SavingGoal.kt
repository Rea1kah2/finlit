package com.finlit.model.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
@Table(name = "saving_goals")
class SavingGoal(

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, precision = 15, scale = 2)
    var targetAmount: BigDecimal,

    @Column(nullable = false, precision = 15, scale = 2)
    var currentAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var deadline: java.time.LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var strategyType: StrategyType = StrategyType.FIXED,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

) : BaseEntity() {

    fun progressPercent(): Int {
        if (targetAmount == BigDecimal.ZERO) return 0
        val percent = currentAmount
            .multiply(BigDecimal(100))
            .divide(targetAmount, 0, RoundingMode.HALF_UP)
        return percent.toInt().coerceAtMost(100)
    }

    fun isAchieved(): Boolean = currentAmount >= targetAmount
}