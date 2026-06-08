package com.finlit.controller

import com.finlit.model.dto.SavingGoalDto
import com.finlit.model.entity.StrategyType
import com.finlit.repository.SavingGoalRepository
import com.finlit.service.SavingGoalService
import com.finlit.service.strategy.AcceleratedStrategy
import com.finlit.service.strategy.FixedAmountStrategy
import com.finlit.service.strategy.PercentageStrategy
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
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Controller
@RequestMapping("/saving-goals")
class SavingGoalController(
    private val savingGoalService: SavingGoalService,
    private val savingGoalRepository: SavingGoalRepository,
    private val fixedStrategy: FixedAmountStrategy,
    private val percentageStrategy: PercentageStrategy,
    private val acceleratedStrategy: AcceleratedStrategy
) {

    @GetMapping
    fun list(
        @AuthenticationPrincipal user: UserDetails,
        model: Model
    ): String {
        val goals = savingGoalService.findByUser(user.username)

        val setoranMap: Map<Long, Map<String, BigDecimal>> = goals
            .filter { it.id != null }
            .associate { goal ->
                val sisaBulan = ChronoUnit.MONTHS
                    .between(LocalDate.now(), goal.deadline)
                    .coerceAtLeast(1L)
                val detailSetoran: Map<String, BigDecimal> = mapOf(
                    "fixed"       to fixedStrategy.hitungSetoran(
                                        goal.targetAmount, goal.currentAmount, sisaBulan),
                    "percentage"  to percentageStrategy.hitungSetoran(
                                        goal.targetAmount, goal.currentAmount, sisaBulan),
                    "accelerated" to acceleratedStrategy.hitungSetoran(
                                        goal.targetAmount, goal.currentAmount, sisaBulan)
                )
                goal.id!! to detailSetoran
            }

        model.addAttribute("goals", goals)
        model.addAttribute("setoranMap", setoranMap)
        return "saving-goal/list"
    }

    @GetMapping("/add")
    fun addForm(model: Model): String {
        model.addAttribute("savingGoalDto", SavingGoalDto())
        model.addAttribute("strategies", StrategyType.values())
        return "saving-goal/form"
    }

    @PostMapping("/add")
    fun save(
        @Valid @ModelAttribute savingGoalDto: SavingGoalDto,
        result: BindingResult,
        @AuthenticationPrincipal user: UserDetails,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        if (result.hasErrors()) {
            model.addAttribute("strategies", StrategyType.values())
            return "saving-goal/form"
        }
        savingGoalService.create(savingGoalDto, user.username)
        redirectAttributes.addFlashAttribute("success", "Target tabungan berhasil dibuat!")
        return "redirect:/saving-goals"
    }

    // ← Endpoint baru: update jumlah tabungan
    @PostMapping("/{id}/update-amount")
    @Transactional
    fun updateAmount(
        @PathVariable id: Long,
        @RequestParam amount: BigDecimal,
        redirectAttributes: RedirectAttributes
    ): String {
        val goal = savingGoalRepository.findById(id)
            .orElseThrow { RuntimeException("Target tidak ditemukan") }

        val oldAmount = goal.currentAmount
        goal.currentAmount = amount.coerceAtLeast(BigDecimal.ZERO)
        savingGoalRepository.save(goal)

        val diff = goal.currentAmount - oldAmount
        val message = when {
            goal.isAchieved() -> "Selamat! Target tabungan ${goal.name} telah tercapai!"
            diff > BigDecimal.ZERO ->
                "Tabungan ${goal.name} berhasil diperbarui. Kamu menabung Rp ${"%,.0f".format(diff)} lebih banyak!"
            else -> "Jumlah tabungan ${goal.name} berhasil diperbarui."
        }
        redirectAttributes.addFlashAttribute("success", message)
        return "redirect:/saving-goals"
    }

    @PostMapping("/delete/{id}")
    fun delete(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        savingGoalService.delete(id)
        redirectAttributes.addFlashAttribute("success", "Target tabungan berhasil dihapus.")
        return "redirect:/saving-goals"
    }
}