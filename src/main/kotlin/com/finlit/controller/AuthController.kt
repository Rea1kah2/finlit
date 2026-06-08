package com.finlit.controller

import com.finlit.model.dto.RegisterDto
import com.finlit.service.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService
) {

    @GetMapping("/login")
    fun loginPage(): String = "auth/login"

    @GetMapping("/register")
    fun registerPage(model: Model): String {
        model.addAttribute("registerDto", RegisterDto())
        return "auth/register"
    }

    @PostMapping("/register")
    fun processRegister(
        @Valid @ModelAttribute("registerDto") dto: RegisterDto,
        result: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {

        // Kalau ada error validasi, kembali ke halaman register
        if (result.hasErrors()) return "auth/register"

        return try {
            userService.register(dto)
            redirectAttributes.addFlashAttribute("success", "Akun berhasil dibuat! Silakan login.")
            "redirect:/auth/login"
        } catch (e: IllegalArgumentException) {
            model.addAttribute("error", e.message)
            "auth/register"
        }
    }
}