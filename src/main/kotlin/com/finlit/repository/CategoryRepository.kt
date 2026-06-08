package com.finlit.repository

import com.finlit.model.entity.Category
import com.finlit.model.entity.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {

    // Ambil semua kategori milik user tertentu
    fun findByUserId(userId: Long): List<Category>

    // Ambil kategori milik user berdasarkan type (INCOME/EXPENSE)
    fun findByUserIdAndType(userId: Long, type: TransactionType): List<Category>
}