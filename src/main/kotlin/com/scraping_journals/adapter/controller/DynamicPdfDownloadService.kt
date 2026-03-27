package com.scraping_journals.adapter.controller

import com.scraping_journals.usecase.service.DownloadPdfService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("download")
class DynamicPdfDownloadService(

    private val downloadPdfService: DownloadPdfService
) {

    @GetMapping("/download-dynamic-pdf", produces = ["application/pdf"])
    fun downloadDynamicPdf(url: String): ResponseEntity<ByteArray?> {

        val response = downloadPdfService.downloadPdf(url)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"download.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(response)
    }


}