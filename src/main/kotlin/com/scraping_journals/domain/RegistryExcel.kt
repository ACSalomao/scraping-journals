package com.scraping_journals.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "registry_excel")
data class RegistryExcel (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val baseDeDados: String?,
    val titulo: String?,
    @Lob
    val abstract: String?,
    val foiIncluido: String?,
    val criterio: String?
)