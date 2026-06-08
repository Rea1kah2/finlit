package com.finlit.service

import com.finlit.model.dto.ProfileDto
import com.finlit.model.dto.RegisterDto
import com.finlit.model.entity.User

interface UserService {
    fun register(dto: RegisterDto): User
    fun findByEmail(email: String): User?
    fun isEmailTaken(email: String): Boolean   
    fun updateProfile(email: String, dto: ProfileDto): User
}