package com.finlit.service.strategy

import com.finlit.service.SavingStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class FixedAmountStrategy : SavingStrategy {

    override fun namaStrategi() = "💰 Setoran Tetap"

    override fun hitungSetoran(
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        sisaBulan: Long
    ): BigDecimal {
        if (sisaBulan <= 0) return (targetAmount - currentAmount).coerceAtLeast(BigDecimal.ZERO)
        val sisaTarget = (targetAmount - currentAmount).coerceAtLeast(BigDecimal.ZERO)
        return sisaTarget.divide(BigDecimal(sisaBulan), 0, RoundingMode.CEILING)
    }

    override fun estimasiBulanSelesai(
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        setoranPerBulan: BigDecimal
    ): Long {
        if (setoranPerBulan <= BigDecimal.ZERO) return -1
        val sisaTarget = (targetAmount - currentAmount).coerceAtLeast(BigDecimal.ZERO)
        return sisaTarget.divide(setoranPerBulan, 0, RoundingMode.CEILING).toLong()
    }
}