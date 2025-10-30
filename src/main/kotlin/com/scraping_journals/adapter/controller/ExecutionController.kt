package com.scraping_journals.adapter.controller

import com.scraping_journals.domain.response.ScrapingDobResponse
import com.scraping_journals.usecase.service.ProcessorScrapingDataService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("main-execution")
class ExecutionController(
    private val processorScrapingDataService: ProcessorScrapingDataService
) {

    @PostMapping("/execute")
    fun execute(@RequestBody scrapingDobResponse: ScrapingDobResponse) {

        // Implement your execution logic here using the scrapingDobResponse data

        processorScrapingDataService.execute(scrapingDobResponse)
        return

    }
}