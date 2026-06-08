package com.finlit.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterDto(
    @field:NotBlank(message = "Nama tidak boleh kosong")
    @field:Size(min = 5, max = 100, message = "Nama harus antara 5 hingga 100 karakter")
    val nama: String = "",

    @field:NotBlank(message = "Email tidak boleh kosong")
    @field:Email(message = "Format email tidak valid")
    val email: String = "",

    @field:NotBlank(message = "Password tidak boleh kosong")
    @field:Size(min = 6, max = 100, message = "Password harus antara 6 hingga 100 karakter")
    val password: String = "",
    
    @field:NotBlank(message = "Konfirmasi password tidak boleh kosong")
    val confirmPassword: String = ""
)