package com.scraping_journals.adapter.controller

import com.scraping_journals.domain.response.ScrapingDobResponse
import com.scraping_journals.usecase.service.ProcessorScrapingDataService
import com.scraping_journals.usecase.service.SemimanualProcessorService
import com.scraping_journals.usecase.service.UtilitiesService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("semimanual-prisma")
class SemimanualPrisma(
    private val processorScrapingDataService: ProcessorScrapingDataService,
    private val semimanualProcessorService: SemimanualProcessorService,
    private val utilitiesService: UtilitiesService
) {

    @PostMapping("/add-reference", consumes = ["multipart/form-data"])
    fun execute(
        @RequestParam("source") source: String,
        @RequestParam("title") title: String,
        @RequestParam("type") type: String?,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {

        // Implement your execution logic here using the scrapingDobResponse data
        semimanualProcessorService.semimanualExecute(source = source, title = title, type = type, file = file)

        return ResponseEntity.ok().body("Prisma added successfully")
    }


       @PostMapping("/generate_xlsx", )
    fun generateXlsx(): ResponseEntity<ByteArray?> {

        val excelBytes = utilitiesService.generatePrismaXlsx()

           val headers = HttpHeaders().apply {
               contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
               set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prisma.xlsx")
           }
           return ResponseEntity.ok()
               .headers(headers)
               .body(excelBytes)
    }
}