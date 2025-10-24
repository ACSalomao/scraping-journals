package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.client.ScrapingDogClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ScrapingDogService(
    private val scrapingDogClient: ScrapingDogClient,
    @param:Value("\${scraping-dog.api.key}") val apiKey: String
) {

    fun searchGoogleScholar(
        query: String,
        maxPages: Int,
        maxResultsPerPage: Int,
        startYear: String,
        endYear: String
    ): String {

        return scrapingDogClient.searchGoogleScholar(
            apiKey =  apiKey,
            query = query,
            asYlo = startYear,
            asYhi = endYear,
            page = maxPages,
            results = maxResultsPerPage
        )
    }
}