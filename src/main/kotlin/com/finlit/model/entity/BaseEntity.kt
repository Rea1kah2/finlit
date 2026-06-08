package com.finlit.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass  // Beritahu JPA: class ini bukan tabel sendiri, tapi diwarisi entity lain
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment dari database
    val id: Long? = null

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate  // Otomatis dipanggil sebelum data diupdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}