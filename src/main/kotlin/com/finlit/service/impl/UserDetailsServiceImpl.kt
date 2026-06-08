package com.finlit.service.impl

import com.finlit.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        // Cari user berdasarkan email
        val user = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User dengan email $email tidak ditemukan") }

        // Kembalikan dalam format yang Spring Security mengerti
        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .authorities(SimpleGrantedAuthority("ROLE_${user.role.name}"))
            .accountLocked(!user.isActive)
            .build()
    }
}