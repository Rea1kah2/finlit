package com.finlit.model.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "transactions")
class Transaction(

    @Column(nullable = false, precision = 15, scale = 2)
    var amount: BigDecimal,         // Jumlah uang (pakai BigDecimal untuk akurasi finansial)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: TransactionType,      // INCOME atau EXPENSE

    @Column(nullable = false, length = 100)
    var description: String,        // Keterangan transaksi

    @Column(nullable = false)
    var transactionDate: LocalDate, // Tanggal transaksi

    @Column(length = 255)
    var notes: String? = null,      // Catatan tambahan (opsional)

    // Relasi ke kategori
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    // Relasi ke user pemilik transaksi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

) : BaseEntity()