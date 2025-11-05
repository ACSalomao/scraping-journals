package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.repository.PrismaRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class PrismaExportService(
    private val prismaRepository: PrismaRepository
) {
    fun exportToExcel(): ByteArray {
        val list = prismaRepository.findAll()

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Prisma")

        // Cabeçalhos
        val header = sheet.createRow(0)
        val columns = listOf("ID", "FONTE", "TITULO", "TIPO", "CLASSIFICAÇÃO", "RESUMO", "METODOLOGIA",  "LINK")
        columns.forEachIndexed { i, name -> header.createCell(i).setCellValue(name) }

        // Dados
        list.forEachIndexed { index, prisma ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(prisma.id.toDouble())
            row.createCell(1).setCellValue(prisma.source)
            row.createCell(2).setCellValue(prisma.title)
            row.createCell(3).setCellValue(prisma.type ?: "")
            row.createCell(4).setCellValue(prisma.methodologyCategory ?: "")
            row.createCell(5).setCellValue(prisma.abstractText ?: "")
            row.createCell(6).setCellValue(prisma.methodologyText ?: "")
            row.createCell(7).setCellValue(prisma.linkToJournal)
        }

        // Ajusta largura das colunas
        columns.indices.forEach { sheet.autoSizeColumn(it) }

        // Converte para ByteArray
        val out = ByteArrayOutputStream()
        workbook.write(out)
        workbook.close()

        return out.toByteArray()
    }
}