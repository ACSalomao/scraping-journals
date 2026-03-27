package com.scraping_journals.adapter.controller

import com.scraping_journals.usecase.service.OpenRouterService
import feign.Body
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/open-router")
class OpenRouterController(
    private val openRouterService: OpenRouterService
) {

    @PostMapping("/ask")
    fun ask(@RequestBody question: String): String =
        openRouterService.ask(question)
}