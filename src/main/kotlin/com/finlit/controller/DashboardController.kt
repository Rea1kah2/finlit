package com.finlit.controller

import com.finlit.repository.SavingGoalRepository
import com.finlit.repository.UserRepository
import com.finlit.service.TransactionService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate

@Controller
@RequestMapping("/dashboard")
class DashboardController(
    private val transactionService: TransactionService,
    private val userRepository: UserRepository,
    private val savingGoalRepository: SavingGoalRepository
) {

    @GetMapping
    @Transactional(readOnly = true)
    fun index(
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val email = userDetails.username
        val user  = userRepository.findByEmail(email).orElseThrow()

        val totalIncome  = transactionService.getTotalIncome(email)
        val totalExpense = transactionService.getTotalExpense(email)
        val balance      = totalIncome - totalExpense

        val recentTransactions = transactionService
            .getTransactionsByUser(email).take(5)

        // Goals yang deadlinenya dalam 30 hari ke depan
        val today     = LocalDate.now()
        val limitDate = today.plusDays(30)
        val upcomingGoals = savingGoalRepository.findUpcomingDeadlines(
            user.id!!, today, limitDate)

        model.addAttribute("nama",               user.nama)
        model.addAttribute("totalIncome",        totalIncome)
        model.addAttribute("totalExpense",       totalExpense)
        model.addAttribute("balance",            balance)
        model.addAttribute("recentTransactions", recentTransactions)
        model.addAttribute("upcomingGoals",      upcomingGoals)

        return "dashboard/index"
    }
}