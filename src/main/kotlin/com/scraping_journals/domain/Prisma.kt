package com.scraping_journals.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "prisma")
data class Prisma(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val source: String,
    val title: String,
    val abstractText: String,
    val methodologyText: String,
    val methodologyCategory: String,
    val linkToJournal: String
)