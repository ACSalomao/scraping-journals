package com.scraping_journals.usecase.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.scraping_journals.adapter.client.ScrapingDogClient
import com.scraping_journals.domain.response.Pagination
import com.scraping_journals.domain.response.ScholarResult
import com.scraping_journals.domain.response.ScrapingDobResponse
import com.scraping_journals.domain.response.SearchDetails
import feign.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class ScrapingDogService(
    private val scrapingDogClient: ScrapingDogClient,
    @param:Value("\${scraping-dog.api.key}") val apiKey: String,
    private val logger: Logger = LoggerFactory.getLogger(ScrapingDogService::class.java)
) {

    fun searchGoogleScholar(
        query: String,
        maxPages: Int,
        maxResultsPerPage: Int,
        startYear: String,
        endYear: String
    ): ScrapingDobResponse? {

        val result = scrapingDogClient.searchGoogleScholar(
            apiKey = apiKey,
            query = query,
            asYlo = startYear,
            asYhi = endYear,
            page = maxPages,
            results = maxResultsPerPage
        )
        return result.body
    }

    fun searchGoogleScholarAllPages(query: String, startYear: String, endYear: String): ScrapingDobResponse {
        var searchDetails: SearchDetails? = null
        val allScholarResults = mutableListOf<ScholarResult>()

        for (page in PAGE_START..PAGE_MAX) {
            logger.info("Scraping page $page")

            val response = scrapingDogClient.searchGoogleScholar(
                apiKey = apiKey,
                query = query,
                asYlo = startYear,
                asYhi = endYear,
                page = page,
                results = MAX_RESULTS_PER_PAGE
            )

            if (page == PAGE_START) {
                searchDetails = response.body?.searchDetails
                logger.info("Search Details: $searchDetails")
            }

            if (response.body == null || response.body!!.scholarResults?.isEmpty() == true) {
                logger.info("No more results found. $page is empty.")
                break
            }

            allScholarResults.addAll(response.body!!.scholarResults ?: emptyList())
        }

        logger.info("Total Scholar Results Collected: ${allScholarResults.size}")

        return ScrapingDobResponse(
            searchDetails = searchDetails,
            profiles = null,
            scholarResults = allScholarResults,
            relatedSearches = null,
            pagination = null,
            scrapingdogPagination = null
        )

    }

    companion object {
        const val PAGE_START = 0
        const val PAGE_MAX = 40
        const val MAX_RESULTS_PER_PAGE = 10
    }
}