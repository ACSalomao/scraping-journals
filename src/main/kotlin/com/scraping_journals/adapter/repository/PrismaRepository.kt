package com.scraping_journals.adapter.repository

import com.scraping_journals.domain.Prisma
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PrismaRepository: JpaRepository<Prisma, Long>