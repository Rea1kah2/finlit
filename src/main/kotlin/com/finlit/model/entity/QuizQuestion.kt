package com.finlit.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "quiz_questions")
class QuizQuestion(

    @Column(nullable = false, columnDefinition = "TEXT")
    var question: String,

    @Column(nullable = false, length = 300)
    var optionA: String,

    @Column(nullable = false, length = 300)
    var optionB: String,

    @Column(nullable = false, length = 300)
    var optionC: String,

    @Column(nullable = false, length = 300)
    var optionD: String,

    @Column(nullable = false, length = 1)
    var correctAnswer: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var explanation: String,

    @Column(nullable = false, length = 50)
    var category: String = "Umum"

) : BaseEntity()