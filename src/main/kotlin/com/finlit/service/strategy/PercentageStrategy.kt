package com.finlit.service.strategy

import com.finlit.service.SavingStrategy
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PercentageStrategy : SavingStrategy {

    override fun namaStrategi() = "📊 20% dari Pemasukan"

    override fun hitungSetoran(
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        sisaBulan: Long
    ): BigDecimal {
        val sisaTarget = (targetAmount - currentAmount).coerceAtLeast(BigDecimal.ZERO)
        val estimasiPemasukan = sisaTarget.divide(BigDecimal(3), 0, RoundingMode.HALF_UP)
        return estimasiPemasukan.multiply(BigDecimal("0.20")).setScale(0, RoundingMode.CEILING)
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