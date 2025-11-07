package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.repository.RegistryExcelRepository
import com.scraping_journals.domain.RegistryExcel
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ExcelService(
    private val registryExcelRepository: RegistryExcelRepository,
    private val utilitiesService: UtilitiesService,
    private val prismaExportService: PrismaExportService,
    private val logger: Logger = LoggerFactory.getLogger(ExcelService::class.java)
) {

    fun processXlsxFile(file: MultipartFile): ByteArray? {

        val xlsxFile = lerArquivo(file)

        for (registryExcel in xlsxFile) {
            logger.info("Processando registro: ${registryExcel.titulo}")

            if (registryExcel.titulo.isNullOrBlank()) {
                logger.warn("Título vazio ou nulo, pulando registro.")
                continue
            }

            if (registryExcelRepository.existsByTituloIgnoreCase(registryExcel.titulo!!)) {
                logger.info("Registro já existe no banco de dados, pulando: ${registryExcel.titulo}")
                continue
            }

            val criterion = utilitiesService.categorizeMethodology(registryExcel.abstract, null)

            val row = RegistryExcel(
                id = 0,
                baseDeDados = registryExcel.baseDeDados,
                titulo = registryExcel.titulo,
                abstract = registryExcel.abstract,
                foiIncluido = registryExcel.foiIncluido,
                criterio = criterion
            )
            try {
                val registroExistente = registryExcelRepository.save(row)
            } catch (e: Exception) {
                logger.error("Erro ao salvar o registro: ${registryExcel.titulo}. Erro: ${e.message}")
            }
        }

        return prismaExportService.exportRegistryToExcel()

    }


    fun lerArquivo(file: MultipartFile): List<RegistryExcel> {
        val registros = mutableListOf<RegistryExcel>()

        file.inputStream.use { input ->
            val workbook = WorkbookFactory.create(input)
            val sheet = workbook.getSheetAt(0)

            // Pular a primeira linha (cabeçalho)
            for (row in sheet.drop(1)) {
                val baseDeDados = getCellValue(row.getCell(0))
                val titulo = getCellValue(row.getCell(1))
                val abstract = getCellValue(row.getCell(2))
                val foiIncluido = getCellValue(row.getCell(3))
                val criterio = getCellValue(row.getCell(4))

                registros.add(
                    RegistryExcel(
                        id = 0,
                        baseDeDados = baseDeDados,
                        titulo = titulo,
                        abstract = abstract,
                        foiIncluido = foiIncluido,
                        criterio = criterio
                    )
                )
            }
            workbook.close()
        }

        return registros
    }

    private fun getCellValue(cell: Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cell.dateCellValue.toString()
                } else {
                    cell.numericCellValue.toLong().toString()
                }
            }

            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> cell.toString()
            else -> ""
        }
    }
}