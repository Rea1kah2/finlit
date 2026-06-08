package com.finlit.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class Category(

    @Column(nullable = false, length = 50)
    var name: String,           // Contoh: "Makan", "Transport", "Gaji"

    @Column(length = 10)
    var icon: String = "📦",   // Emoji icon untuk tampilan

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: TransactionType,  // INCOME atau EXPENSE

    // Relasi: banyak kategori dimiliki satu user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User

) : BaseEntity()