package com.finlit.controller

import com.finlit.model.entity.Role
import com.finlit.repository.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin")
class AdminController(
    private val userRepository: UserRepository
) {

    @GetMapping("/users")
    @Transactional(readOnly = true)
    fun listUsers(
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val currentUser = userRepository.findByEmail(userDetails.username).orElseThrow()
        if (currentUser.role != Role.ADMIN) return "redirect:/dashboard"

        val users = userRepository.findAll()
        model.addAttribute("users", users)
        model.addAttribute("currentUserId", currentUser.id)
        return "admin/users"
    }

    @PostMapping("/users/{id}/toggle-active")
    @Transactional
    fun toggleActive(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentUser = userRepository.findByEmail(userDetails.username).orElseThrow()
        if (currentUser.role != Role.ADMIN) return "redirect:/dashboard"

        val user = userRepository.findById(id).orElseThrow()
        if (user.id != currentUser.id) {
            user.isActive = !user.isActive
            userRepository.save(user)
            val status = if (user.isActive) "diaktifkan" else "dinonaktifkan"
            redirectAttributes.addFlashAttribute("success", "User ${user.nama} berhasil $status.")
        }
        return "redirect:/admin/users"
    }

    @PostMapping("/users/{id}/toggle-role")
    @Transactional
    fun toggleRole(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentUser = userRepository.findByEmail(userDetails.username).orElseThrow()
        if (currentUser.role != Role.ADMIN) return "redirect:/dashboard"

        val user = userRepository.findById(id).orElseThrow()
        if (user.id != currentUser.id) {
            user.role = if (user.role == Role.ADMIN) Role.USER else Role.ADMIN
            userRepository.save(user)
            redirectAttributes.addFlashAttribute("success", "Role user ${user.nama} berhasil diubah.")
        }
        return "redirect:/admin/users"
    }
}