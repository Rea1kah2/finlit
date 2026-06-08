package com.finlit.controller

import com.finlit.model.dto.ProfileDto
import com.finlit.repository.UserRepository
import com.finlit.service.UserService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val userService: UserService,
    private val userRepository: UserRepository
) {

    @GetMapping
    @Transactional(readOnly = true)
    fun profilePage(
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()
        model.addAttribute("user", user)
        model.addAttribute("profileDto", ProfileDto(nama = user.nama))
        return "profile/index"
    }

    @PostMapping
    fun updateProfile(
        @Valid @ModelAttribute profileDto: ProfileDto,
        result: BindingResult,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        if (result.hasErrors()) {
            val user = userRepository.findByEmail(userDetails.username).orElseThrow()
            model.addAttribute("user", user)
            return "profile/index"
        }

        return try {
            userService.updateProfile(userDetails.username, profileDto)
            redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui!")
            "redirect:/profile"
        } catch (e: IllegalArgumentException) {
            val user = userRepository.findByEmail(userDetails.username).orElseThrow()
            model.addAttribute("user", user)
            model.addAttribute("error", e.message)
            "profile/index"
        }
    }
}