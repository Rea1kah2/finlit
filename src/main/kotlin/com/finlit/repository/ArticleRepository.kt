package com.finlit.repository

import com.finlit.model.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByIsPublishedTrueOrderByCreatedAtDesc(): List<Article>
    fun findByCategoryAndIsPublishedTrue(category: String): List<Article>
    fun findByAuthorIdOrderByCreatedAtDesc(authorId: Long): List<Article>
}