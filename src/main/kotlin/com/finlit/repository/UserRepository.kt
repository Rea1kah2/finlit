package com.finlit.repository

import com.finlit.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    // Spring otomatis generate SQL: SELECT * FROM users WHERE email = ?
    fun findByEmail(email: String): Optional<User>

    // Spring otomatis generate SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    fun existsByEmail(email: String): Boolean
}