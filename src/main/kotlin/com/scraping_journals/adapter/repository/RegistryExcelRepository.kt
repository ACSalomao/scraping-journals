package com.scraping_journals.adapter.repository

import com.scraping_journals.domain.RegistryExcel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistryExcelRepository : JpaRepository<RegistryExcel, Long> {
    fun existsByTituloIgnoreCase(titulo: String): Boolean
}