package com.finlit.controller

import com.finlit.model.entity.TransactionType
import com.finlit.repository.TransactionRepository
import com.finlit.repository.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller
@RequestMapping("/reports")
class ReportController(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) {

    @GetMapping
    @Transactional(readOnly = true)
    fun report(
        @RequestParam(required = false) month: Int?,
        @RequestParam(required = false) year: Int?,
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val now           = LocalDate.now()
        val selectedMonth = month ?: now.monthValue
        val selectedYear  = year  ?: now.year
        val user          = userRepository.findByEmail(userDetails.username).orElseThrow()
        val userId        = user.id!!
        val startDate     = LocalDate.of(selectedYear, selectedMonth, 1)
        val endDate       = startDate.withDayOfMonth(startDate.lengthOfMonth())

        val totalIncome  = transactionRepository.sumByUserIdAndTypeAndDateBetween(
            userId, TransactionType.INCOME, startDate, endDate)
        val totalExpense = transactionRepository.sumByUserIdAndTypeAndDateBetween(
            userId, TransactionType.EXPENSE, startDate, endDate)

        val expenseByCategory = transactionRepository.sumByCategoryAndDateBetween(
            userId, TransactionType.EXPENSE, startDate, endDate)

        val pieLabels = expenseByCategory.map { "\"${it[0]}\"" }
        val pieData   = expenseByCategory.map { it[1].toString() }

        val barLabels  = mutableListOf<String>()
        val barIncome  = mutableListOf<String>()
        val barExpense = mutableListOf<String>()

        for (i in 5 downTo 0) {
            val d     = now.minusMonths(i.toLong())
            val s     = LocalDate.of(d.year, d.monthValue, 1)
            val e     = s.withDayOfMonth(s.lengthOfMonth())
            barLabels.add("\"${d.format(DateTimeFormatter.ofPattern("MMM yy"))}\"")
            barIncome.add(transactionRepository.sumByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, s, e).toString())
            barExpense.add(transactionRepository.sumByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, s, e).toString())
        }

        val transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate)

        model.addAttribute("selectedMonth", selectedMonth)
        model.addAttribute("selectedYear",  selectedYear)
        model.addAttribute("totalIncome",   totalIncome)
        model.addAttribute("totalExpense",  totalExpense)
        model.addAttribute("balance",       totalIncome - totalExpense)
        model.addAttribute("pieLabels",     "[${pieLabels.joinToString(",")}]")
        model.addAttribute("pieData",       "[${pieData.joinToString(",")}]")
        model.addAttribute("barLabels",     "[${barLabels.joinToString(",")}]")
        model.addAttribute("barIncome",     "[${barIncome.joinToString(",")}]")
        model.addAttribute("barExpense",    "[${barExpense.joinToString(",")}]")
        model.addAttribute("hasPieData",    expenseByCategory.isNotEmpty())
        model.addAttribute("transactions",  transactions)
        model.addAttribute("months",        (1..12).toList())
        model.addAttribute("years",         (now.year - 2..now.year).toList())

        return "report/index"
    }
}