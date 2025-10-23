package com.scraping_journals.adapter.repository

import com.scraping_journals.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>