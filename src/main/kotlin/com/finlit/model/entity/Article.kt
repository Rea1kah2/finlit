package com.finlit.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "articles")
class Article(

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(nullable = false, length = 100)
    var category: String,       // Contoh: "Investasi", "Budgeting", "Tabungan"

    @Column(nullable = false, length = 500)
    var summary: String,        // Ringkasan singkat

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,        // Isi artikel lengkap

    @Column(length = 10)
    var emoji: String = "📄",   // Icon artikel

    @Column(nullable = false)
    var isPublished: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User            // Yang menulis artikel (Admin)

) : BaseEntity()