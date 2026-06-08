package com.finlit.model.dto

import com.finlit.model.entity.TransactionType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionDto(

    @field:NotNull(message = "Jumlah tidak boleh kosong")
    @field:DecimalMin(value = "0.01", message = "Jumlah harus lebih dari 0")
    val amount: BigDecimal? = null,

    @field:NotNull(message = "Jenis transaksi harus dipilih")
    val type: TransactionType? = null,

    @field:NotBlank(message = "Deskripsi tidak boleh kosong")
    @field:Size(max = 100, message = "Deskripsi maksimal 100 karakter")
    val description: String = "",

    @field:NotNull(message = "Tanggal tidak boleh kosong")
    val transactionDate: LocalDate? = LocalDate.now(),

    val notes: String? = null,

    @field:NotNull(message = "Kategori harus dipilih")
    val categoryId: Long? = null
)