package com.scraping_journals.adapter.controller

import com.scraping_journals.adapter.client.GenericDownloadClient
import com.scraping_journals.adapter.client.UnsafeWebClientFactory
import feign.Feign
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("download")
class DynamicPdfDownloadService(
    private val client: GenericDownloadClient,
    private val unsafeWebClientFactory: UnsafeWebClientFactory
//    private val feignBuilder: Feign.Builder
) {

    @GetMapping("/download-dynamic-pdf", produces = ["application/pdf"])
    fun downloadDynamicPdf(url: String): ResponseEntity<ByteArray?> {
//        val client = feignBuilder.target(GenericDownloadClient::class.java, extrairDominio(url))
        val response = downloadPdf(url)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"download.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(response)
    }

    fun downloadPdf(url: String): ByteArray {
        val webClient = unsafeWebClientFactory.create()
        return webClient.get()
            .uri(url)
            .accept(MediaType.APPLICATION_PDF)
            .retrieve()
            .bodyToMono(ByteArray::class.java)
            .block() ?: throw RuntimeException("Erro ao baixar PDF")
    }
}