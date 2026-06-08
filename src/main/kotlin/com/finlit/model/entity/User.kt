package com.finlit.model.entity

import jakarta.persistence.*

// Enum untuk role pengguna
enum class Role {
    USER,   // Mahasiswa / Gen Z biasa
    ADMIN   // Administrator sistem
}

@Entity
@Table(name = "users")  // Nama tabel di PostgreSQL
class User(

    @Column(nullable = false, unique = true, length = 100)
    var email: String,

    @Column(nullable = false, length = 100)
    var nama: String,

    @Column(nullable = false)
    var password: String,  // Akan di-hash dengan BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER,  // Default: USER biasa

    @Column(nullable = false)
    var isActive: Boolean = true

) : BaseEntity()  // Mewarisi BaseEntity → dapat id, createdAt, updatedAt