package com.scraping_journals.domain.request

import com.scraping_journals.domain.ChatMessage

data class ChatRequest(val model: String, val messages: List<ChatMessage>)
