package com.finlit.service

import com.finlit.model.dto.TransactionDto
import com.finlit.model.entity.Transaction
import java.math.BigDecimal

interface TransactionService {
    fun addTransaction(dto: TransactionDto, userEmail: String): Transaction
    fun getTransactionsByUser(userEmail: String): List<Transaction>
    fun deleteTransaction(id: Long, userEmail: String)
    fun getTotalIncome(userEmail: String): BigDecimal
    fun getTotalExpense(userEmail: String): BigDecimal
}