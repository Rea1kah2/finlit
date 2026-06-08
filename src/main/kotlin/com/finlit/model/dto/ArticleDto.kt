package com.finlit.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ArticleDto(

    @field:NotBlank(message = "Judul tidak boleh kosong")
    @field:Size(max = 200, message = "Judul maksimal 200 karakter")
    val title: String = "",

    @field:NotBlank(message = "Kategori tidak boleh kosong")
    val category: String = "",

    @field:NotBlank(message = "Ringkasan tidak boleh kosong")
    @field:Size(max = 500, message = "Ringkasan maksimal 500 karakter")
    val summary: String = "",

    @field:NotBlank(message = "Konten tidak boleh kosong")
    val content: String = "",

    val emoji: String = "📄",

    val isPublished: Boolean = true
)