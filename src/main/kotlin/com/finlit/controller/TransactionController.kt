package com.finlit.controller

import com.finlit.model.dto.TransactionDto
import com.finlit.model.entity.TransactionType
import com.finlit.repository.CategoryRepository
import com.finlit.repository.TransactionRepository
import com.finlit.repository.UserRepository
import com.finlit.service.TransactionService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate

@Controller
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) {

    @GetMapping
    @Transactional(readOnly = true)
    fun listTransactions(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) startDate: String?,
        @RequestParam(required = false) endDate: String?,
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()

        val typeEnum  = if (!type.isNullOrBlank()) TransactionType.valueOf(type) else null
        val start     = if (!startDate.isNullOrBlank()) LocalDate.parse(startDate) else null
        val end       = if (!endDate.isNullOrBlank()) LocalDate.parse(endDate) else null
        val kw        = keyword?.takeIf { it.isNotBlank() }?.lowercase()

        val all = transactionRepository.findByUserIdOrderByTransactionDateDesc(user.id!!)
        val transactions = all.filter { t ->
            (kw == null || t.description.lowercase().contains(kw)) &&
            (typeEnum == null || t.type == typeEnum) &&
            (categoryId == null || t.category.id == categoryId) &&
            (start == null || !t.transactionDate.isBefore(start)) &&
            (end == null || !t.transactionDate.isAfter(end))
        }

        val totalIncome  = transactionService.getTotalIncome(userDetails.username)
        val totalExpense = transactionService.getTotalExpense(userDetails.username)
        val categories   = categoryRepository.findByUserId(user.id!!)

        model.addAttribute("transactions",   transactions)
        model.addAttribute("totalIncome",    totalIncome)
        model.addAttribute("totalExpense",   totalExpense)
        model.addAttribute("balance",        totalIncome - totalExpense)
        model.addAttribute("categories",     categories)
        model.addAttribute("keyword",        keyword ?: "")
        model.addAttribute("selectedType",   type ?: "")
        model.addAttribute("categoryId",     categoryId)
        model.addAttribute("startDate",      startDate ?: "")
        model.addAttribute("endDate",        endDate ?: "")
        return "transaction/list"
    }

    @GetMapping("/add")
    @Transactional(readOnly = true)
    fun addForm(
        @AuthenticationPrincipal userDetails: UserDetails,
        model: Model
    ): String {
        val user = userRepository.findByEmail(userDetails.username).orElseThrow()
        model.addAttribute("transactionDto", TransactionDto())
        model.addAttribute("categories", categoryRepository.findByUserId(user.id!!))
        model.addAttribute("types", TransactionType.values())
        return "transaction/form"
    }

    @PostMapping("/add")
    fun saveTransaction(
        @Valid @ModelAttribute transactionDto: TransactionDto,
        result: BindingResult,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        if (result.hasErrors()) {
            val user = userRepository.findByEmail(userDetails.username).orElseThrow()
            model.addAttribute("categories", categoryRepository.findByUserId(user.id!!))
            model.addAttribute("types", TransactionType.values())
            return "transaction/form"
        }
        transactionService.addTransaction(transactionDto, userDetails.username)
        redirectAttributes.addFlashAttribute("success", "Transaksi berhasil ditambahkan!")
        return "redirect:/transactions"
    }

    @PostMapping("/delete/{id}")
    fun deleteTransaction(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        transactionService.deleteTransaction(id, userDetails.username)
        redirectAttributes.addFlashAttribute("success", "Transaksi berhasil dihapus!")
        return "redirect:/transactions"
    }
}