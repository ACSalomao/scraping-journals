package com.scraping_journals.adapter.controller

import com.scraping_journals.usecase.service.ScrapingDogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scraping-dog")
class ScrapingDogController(
    private val scrapingDogService: ScrapingDogService
) {

    @PostMapping("/scrape-google-scholar")
    fun scrapeGoogleScholar(
        @RequestParam("query") query: String,
        @RequestParam("max_pages") maxPages: Int,
        @RequestParam("max_results_per_page") maxResultsPerPage: Int,
        @RequestParam("start_year") startYear: String,
        @RequestParam("end_year") endYear: String
    ): ResponseEntity<String?> {

        val result = scrapingDogService.searchGoogleScholar(
            query = query,
            maxPages = maxPages,
            maxResultsPerPage = maxResultsPerPage,
            startYear = startYear,
            endYear = endYear
        )
        return ResponseEntity.ok(result)
    }
}