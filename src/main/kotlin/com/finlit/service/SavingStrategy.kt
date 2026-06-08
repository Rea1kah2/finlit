package com.finlit.service

import java.math.BigDecimal

interface SavingStrategy {
    fun namaStrategi(): String
    fun hitungSetoran(targetAmount: BigDecimal, currentAmount: BigDecimal, sisaBulan: Long): BigDecimal
    fun estimasiBulanSelesai(targetAmount: BigDecimal, currentAmount: BigDecimal, setoranPerBulan: BigDecimal): Long
}