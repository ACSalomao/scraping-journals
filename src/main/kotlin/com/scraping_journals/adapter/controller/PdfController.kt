package com.scraping_journals.adapter.controller

import com.scraping_journals.usecase.handler.PdfHandler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@RequestMapping("pdf")
class PdfController(
    private val pdfHandler: PdfHandler
) {

    @PostMapping("/extract-abstract", consumes = ["multipart/form-data"])
    fun extractAbstract(@RequestParam("file") file: MultipartFile): ResponseEntity<String?> {
        val tempFile = File.createTempFile("uploaded-", ".pdf")
        file.transferTo(tempFile)

        val abstractText = pdfHandler.extractAbstract(tempFile.path)
        tempFile.delete() // Limpeza do arquivo temporário

        return ResponseEntity.ok(abstractText ?: "Abstract not found")
    }

    @PostMapping("/extract-abstract-br", consumes = ["multipart/form-data"])
    fun extractAbstractBr(@RequestParam("file") file: MultipartFile): ResponseEntity<String?> {
        val tempFile = File.createTempFile("uploaded-", ".pdf")
        file.transferTo(tempFile)

        val abstractText = pdfHandler.extractAbstractBr(tempFile.path)
        tempFile.delete() // Limpeza do arquivo temporário

        return ResponseEntity.ok(abstractText ?: "Abstract not found")
    }
}