package com.finlit.controller

import com.finlit.repository.QuizRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/quiz")
class QuizController(
    private val quizRepository: QuizRepository
) {

    @GetMapping
    fun startPage(model: Model): String {
        val totalSoal = quizRepository.count()
        model.addAttribute("totalSoal", totalSoal)
        return "quiz/start"
    }

    @GetMapping("/play")
    fun playQuiz(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(defaultValue = "") answers: String,
        model: Model
    ): String {
        val questions = quizRepository.findAll()
        if (questions.isEmpty()) return "redirect:/quiz"
        if (index >= questions.size) return "redirect:/quiz/result?answers=$answers"

        val question  = questions[index]
        val totalSoal = questions.size
        val progress  = ((index.toDouble() / totalSoal) * 100).toInt()

        model.addAttribute("question",  question)
        model.addAttribute("index",     index)
        model.addAttribute("totalSoal", totalSoal)
        model.addAttribute("progress",  progress)
        model.addAttribute("answers",   answers)
        return "quiz/question"
    }

    @PostMapping("/answer")
    fun submitAnswer(
        @RequestParam index: Int,
        @RequestParam answer: String,
        @RequestParam(defaultValue = "") answers: String
    ): String {
        val newAnswers = if (answers.isEmpty()) answer else "$answers,$answer"
        return "redirect:/quiz/play?index=${index + 1}&answers=$newAnswers"
    }

    @GetMapping("/result")
    fun showResult(
        @RequestParam(defaultValue = "") answers: String,
        model: Model
    ): String {
        val questions  = quizRepository.findAll()
        val answerList = if (answers.isEmpty()) emptyList() else answers.split(",")

        var score = 0
        val reviewList = mutableListOf<Map<String, Any>>()

        questions.forEachIndexed { i, q ->
            val userAnswer = answerList.getOrElse(i) { "" }
            val isCorrect  = userAnswer == q.correctAnswer
            if (isCorrect) score++

            reviewList.add(mapOf(
                "question"      to q.question,
                "optionA"       to q.optionA,
                "optionB"       to q.optionB,
                "optionC"       to q.optionC,
                "optionD"       to q.optionD,
                "userAnswer"    to userAnswer,
                "correctAnswer" to q.correctAnswer,
                "explanation"   to q.explanation,
                "isCorrect"     to isCorrect
            ))
        }

        val totalSoal  = questions.size
        val percentage = if (totalSoal > 0) (score * 100) / totalSoal else 0
        val grade = when {
            percentage >= 80 -> "Luar Biasa!"
            percentage >= 60 -> "Cukup Baik"
            percentage >= 40 -> "Perlu Belajar Lagi"
            else             -> "Terus Semangat!"
        }

        model.addAttribute("score",      score)
        model.addAttribute("totalSoal",  totalSoal)
        model.addAttribute("percentage", percentage)
        model.addAttribute("grade",      grade)
        model.addAttribute("reviewList", reviewList)
        return "quiz/result"
    }
}