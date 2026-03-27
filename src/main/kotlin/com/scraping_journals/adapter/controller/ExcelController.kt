package com.scraping_journals.adapter.controller

import com.scraping_journals.domain.RegistryExcel
import com.scraping_journals.usecase.service.ExcelService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/xlsx")
class ExcelController(private val excelService: ExcelService) {
    @PostMapping("/upload", consumes = ["multipart/form-data"])
    fun uploadArquivo(@RequestParam("file") file: MultipartFile): ResponseEntity<ByteArray?> {
        require(!file.isEmpty) { "Arquivo XLSX não pode estar vazio." }

        val excelBytes = excelService.processXlsxFile(file)

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prismaSlim.xlsx")
        }
        return ResponseEntity.ok()
            .headers(headers)
            .body(excelBytes)
    }
}