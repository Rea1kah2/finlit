package com.finlit.controller

import com.finlit.model.dto.ArticleDto
import com.finlit.model.entity.Article
import com.finlit.model.entity.Role
import com.finlit.repository.ArticleRepository
import com.finlit.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/articles")
class ArticleController(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {

    // Semua user: lihat daftar artikel
    @GetMapping
    @Transactional(readOnly = true)
    fun list(
        @RequestParam(required = false) category: String?,
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()

        val articles = if (category != null)
            articleRepository.findByCategoryAndIsPublishedTrue(category)
        else
            articleRepository.findByIsPublishedTrueOrderByCreatedAtDesc()

        val categories = articleRepository
            .findByIsPublishedTrueOrderByCreatedAtDesc()
            .map { it.category }
            .distinct()

        model.addAttribute("articles", articles)
        model.addAttribute("categories", categories)
        model.addAttribute("selectedCategory", category)
        model.addAttribute("isAdmin", user.role == Role.ADMIN)
        return "article/list"
    }

    // Semua user: baca artikel
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    fun detail(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val article = articleRepository.findById(id)
            .orElseThrow { RuntimeException("Artikel tidak ditemukan") }
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()

        model.addAttribute("article", article)
        model.addAttribute("isAdmin", user.role == Role.ADMIN)
        return "article/detail"
    }

    // Hanya ADMIN: form buat artikel
    @GetMapping("/create")
    fun createForm(
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()
        if (user.role != Role.ADMIN) return "redirect:/articles"

        model.addAttribute("articleDto", ArticleDto())
        return "article/form"
    }

    // Hanya ADMIN: simpan artikel baru
    @PostMapping("/create")
    @Transactional
    fun saveArticle(
        @Valid @ModelAttribute articleDto: ArticleDto,
        result: BindingResult,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()
        if (user.role != Role.ADMIN) return "redirect:/articles"

        if (result.hasErrors()) return "article/form"

        val article = Article(
            title       = articleDto.title,
            category    = articleDto.category,
            summary     = articleDto.summary,
            content     = articleDto.content,
            emoji       = articleDto.emoji.ifBlank { "📄" },
            isPublished = articleDto.isPublished,
            author      = user
        )
        articleRepository.save(article)
        redirectAttributes.addFlashAttribute("success", "Artikel berhasil dipublikasikan!")
        return "redirect:/articles"
    }

    // Hanya ADMIN: hapus artikel
    @PostMapping("/delete/{id}")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()
        if (user.role != Role.ADMIN) return "redirect:/articles"

        articleRepository.deleteById(id)
        redirectAttributes.addFlashAttribute("success", "Artikel berhasil dihapus.")
        return "redirect:/articles"
    }
}