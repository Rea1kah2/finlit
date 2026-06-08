package com.finlit.config

import com.finlit.model.entity.User
import com.finlit.repository.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalControllerAdvice(
    private val userRepository: UserRepository
) {
    @ModelAttribute("currentUser")
    @Transactional(readOnly = true)
    fun addCurrentUser(@AuthenticationPrincipal userDetails: UserDetails?): User? {
        return userDetails?.let {
            userRepository.findByEmail(it.username).orElse(null)
        }
    }
}