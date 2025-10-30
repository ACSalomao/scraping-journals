package com.scraping_journals.configuration

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class OpenRouterConfig {

    @Bean
    fun requestInterceptor(@Value("\${openrouter.api-key}") apiKey: String): RequestInterceptor {
        return RequestInterceptor { template ->
            template.header("Authorization", "Bearer $apiKey")
            template.header("Content-Type", "application/json")
        }
    }
}