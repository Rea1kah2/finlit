package com.finlit.model.dto

import com.finlit.model.entity.StrategyType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class SavingGoalDto(

    @field:NotBlank(message = "Nama target tidak boleh kosong")
    val name: String = "",

    @field:NotNull(message = "Jumlah target tidak boleh kosong")
    @field:DecimalMin(value = "1000", message = "Target minimal Rp 1.000")
    val targetAmount: BigDecimal? = null,

    @field:NotNull(message = "Jumlah saat ini tidak boleh kosong")
    val currentAmount: BigDecimal? = BigDecimal.ZERO,

    @field:NotNull(message = "Deadline tidak boleh kosong")
    val deadline: LocalDate? = null,

    val strategyType: StrategyType? = StrategyType.FIXED
)