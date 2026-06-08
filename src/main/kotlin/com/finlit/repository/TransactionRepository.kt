package com.finlit.repository

import com.finlit.model.entity.Transaction
import com.finlit.model.entity.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction t JOIN FETCH t.category
        WHERE t.user.id = :userId
        ORDER BY t.transactionDate DESC
    """)
    fun findByUserIdOrderByTransactionDateDesc(
        @Param("userId") userId: Long
    ): List<Transaction>

    @Query("""
        SELECT t FROM Transaction t JOIN FETCH t.category
        WHERE t.user.id = :userId
        AND t.transactionDate BETWEEN :start AND :end
        ORDER BY t.transactionDate DESC
    """)
    fun findByUserIdAndDateBetween(
        @Param("userId") userId: Long,
        @Param("start") start: LocalDate,
        @Param("end") end: LocalDate
    ): List<Transaction>

    @Query("""
        SELECT t FROM Transaction t JOIN FETCH t.category
        WHERE t.user.id = :userId
        AND (:keyword IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:type IS NULL OR t.type = :type)
        AND (:categoryId IS NULL OR t.category.id = :categoryId)
        AND (:startDate IS NULL OR t.transactionDate >= :startDate)
        AND (:endDate IS NULL OR t.transactionDate <= :endDate)
        ORDER BY t.transactionDate DESC
    """)
    fun findWithFilters(
        @Param("userId") userId: Long,
        @Param("keyword") keyword: String?,
        @Param("type") type: TransactionType?,
        @Param("categoryId") categoryId: Long?,
        @Param("startDate") startDate: LocalDate?,
        @Param("endDate") endDate: LocalDate?
    ): List<Transaction>

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.user.id = :userId AND t.type = :type
    """)
    fun sumAmountByUserIdAndType(
        @Param("userId") userId: Long,
        @Param("type") type: TransactionType
    ): BigDecimal

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.user.id = :userId
        AND t.type = :type
        AND t.transactionDate BETWEEN :start AND :end
    """)
    fun sumByUserIdAndTypeAndDateBetween(
        @Param("userId") userId: Long,
        @Param("type") type: TransactionType,
        @Param("start") start: LocalDate,
        @Param("end") end: LocalDate
    ): BigDecimal

    @Query("""
        SELECT t.category.name, COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user.id = :userId
        AND t.type = :type
        AND t.transactionDate BETWEEN :start AND :end
        GROUP BY t.category.name
    """)
    fun sumByCategoryAndDateBetween(
        @Param("userId") userId: Long,
        @Param("type") type: TransactionType,
        @Param("start") start: LocalDate,
        @Param("end") end: LocalDate
    ): List<Array<Any>>
}