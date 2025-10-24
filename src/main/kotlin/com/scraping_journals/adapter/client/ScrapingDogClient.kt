package com.scraping_journals.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "scrapingDogClient", url = "\${scraping-dog.api.url}")
interface ScrapingDogClient {

    @GetMapping("/google_scholar")
    fun searchGoogleScholar(
        @RequestParam("api_key") apiKey: String,
        @RequestParam("query") query: String,
        @RequestParam("language") language: String = "pt-br",
        @RequestParam("as_ylo") asYlo: String? = null,
        @RequestParam("as_yhi") asYhi: String? = null,
        @RequestParam("page") page: Int,
        @RequestParam("results") results: Int
    ): String
}