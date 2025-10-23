package com.scraping_journals.adapter.controller

import com.scraping_journals.adapter.repository.UserRepository
import com.scraping_journals.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("journals")
class JournalsController(
    private val userRepository: UserRepository,
    private val logger: Logger = LoggerFactory.getLogger(JournalsController::class.java)
) {

    @PostMapping("import-file")
    fun importFile(): ResponseEntity.BodyBuilder {

        return ResponseEntity.ok()
    }


    @GetMapping("adduser")
    fun adduser(): ResponseEntity<String> {
        logger.info("Saving User")
        userRepository.save(User(name = "John Doe"))
        return ResponseEntity.ok("User added successfully")
    }
}