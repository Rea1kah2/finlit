package com.finlit.service.impl

import com.finlit.model.dto.TransactionDto
import com.finlit.model.entity.Transaction
import com.finlit.model.entity.TransactionType
import com.finlit.repository.CategoryRepository
import com.finlit.repository.TransactionRepository
import com.finlit.repository.UserRepository
import com.finlit.service.TransactionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional  // ← Kunci fix: session Hibernate tetap aktif selama method berjalan
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) : TransactionService {

    override fun addTransaction(dto: TransactionDto, userEmail: String): Transaction {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User tidak ditemukan") }

        val category = categoryRepository.findById(dto.categoryId!!)
            .orElseThrow { RuntimeException("Kategori tidak ditemukan") }

        val transaction = Transaction(
            amount        = dto.amount!!,
            type          = dto.type!!,
            description   = dto.description,
            transactionDate = dto.transactionDate!!,
            notes         = dto.notes,
            category      = category,
            user          = user
        )
        return transactionRepository.save(transaction)
    }

    @Transactional(readOnly = true)  // readOnly = lebih efisien untuk query
    override fun getTransactionsByUser(userEmail: String): List<Transaction> {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User tidak ditemukan") }
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(user.id!!)
    }

    override fun deleteTransaction(id: Long, userEmail: String) {
        transactionRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getTotalIncome(userEmail: String): BigDecimal {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User tidak ditemukan") }
        return transactionRepository.sumAmountByUserIdAndType(user.id!!, TransactionType.INCOME)
    }

    @Transactional(readOnly = true)
    override fun getTotalExpense(userEmail: String): BigDecimal {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { RuntimeException("User tidak ditemukan") }
        return transactionRepository.sumAmountByUserIdAndType(user.id!!, TransactionType.EXPENSE)
    }
}