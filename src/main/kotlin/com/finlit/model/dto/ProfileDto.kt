package com.finlit.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ProfileDto(

    @field:NotBlank(message = "Nama tidak boleh kosong")
    @field:Size(min = 2, max = 100, message = "Nama harus 2-100 karakter")
    val nama: String = "",

    val currentPassword: String = "",

    @field:Size(min = 6, message = "Password baru minimal 6 karakter")
    val newPassword: String = "",

    val confirmPassword: String = ""
)