package com.scraping_journals.adapter.client

import com.scraping_journals.configuration.OpenRouterConfig
import com.scraping_journals.domain.request.ChatRequest
import com.scraping_journals.domain.response.ChatResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "openRouterClient",
    url = "\${openrouter.api-base-url}",
    configuration = [OpenRouterConfig::class]
)
interface OpenRouterClient {
     @PostMapping("/chat/completions")
    fun chat(@RequestBody request: ChatRequest): ChatResponse

    @PostMapping("/chat/completions")
    fun askQuestion(@RequestBody request: ChatRequest): ChatResponse
}