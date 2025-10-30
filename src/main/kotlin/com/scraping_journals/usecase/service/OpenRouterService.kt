package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.client.OpenRouterClient
import com.scraping_journals.domain.ChatMessage
import com.scraping_journals.domain.request.ChatRequest
import org.springframework.stereotype.Service

@Service
class OpenRouterService(
    private val client: OpenRouterClient
) {
    fun ask(question: String): String {
        val request = ChatRequest(
            model = "openai/gpt-oss-20b:free", // modelo leve e rápido
            messages = listOf(ChatMessage("user", question))
        )

        val response = client.chat(request)
        return response.choices?.firstOrNull()?.message?.content ?: "Sem resposta."
    }
}