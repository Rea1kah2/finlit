package com.finlit.service.impl

import com.finlit.model.dto.ProfileDto
import com.finlit.model.dto.RegisterDto
import com.finlit.model.entity.Role
import com.finlit.model.entity.User
import com.finlit.repository.UserRepository
import com.finlit.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun register(dto: RegisterDto): User {
        if (dto.password != dto.confirmPassword)
            throw IllegalArgumentException("Password dan konfirmasi password tidak sama")
        if (userRepository.existsByEmail(dto.email))
            throw IllegalArgumentException("Email ${dto.email} sudah terdaftar")

        val user = User(
            email    = dto.email.lowercase().trim(),
            nama     = dto.nama.trim(),
            password = passwordEncoder.encode(dto.password),
            role     = Role.USER
        )
        return userRepository.save(user)
    }

    override fun findByEmail(email: String): User? =
        userRepository.findByEmail(email).orElse(null)

    override fun isEmailTaken(email: String): Boolean =
        userRepository.existsByEmail(email)

    override fun updateProfile(email: String, dto: ProfileDto): User {
        val user = userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("User tidak ditemukan") }

        user.nama = dto.nama.trim()

        if (dto.newPassword.isNotBlank()) {
            if (!passwordEncoder.matches(dto.currentPassword, user.password))
                throw IllegalArgumentException("Password saat ini tidak sesuai")
            if (dto.newPassword != dto.confirmPassword)
                throw IllegalArgumentException("Password baru dan konfirmasi tidak sama")
            user.password = passwordEncoder.encode(dto.newPassword)
        }

        return userRepository.save(user)
    }
}