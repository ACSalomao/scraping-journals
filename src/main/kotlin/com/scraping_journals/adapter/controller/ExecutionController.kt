package com.scraping_journals.adapter.controller

import com.scraping_journals.domain.response.ScrapingDobResponse
import com.scraping_journals.usecase.service.ProcessorScrapingDataService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
    fun execute(@RequestBody scrapingDobResponse: ScrapingDobResponse): ResponseEntity<ByteArray?> {

        // Implement your execution logic here using the scrapingDobResponse data
        val excelBytes = processorScrapingDataService.execute(scrapingDobResponse)

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prisma.xlsx")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .body(excelBytes)
    }
}