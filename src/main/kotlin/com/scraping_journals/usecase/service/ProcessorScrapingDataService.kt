package com.scraping_journals.usecase.service


import com.scraping_journals.adapter.repository.PrismaRepository
import com.scraping_journals.domain.Prisma
import com.scraping_journals.domain.response.ScrapingDobResponse
import com.scraping_journals.usecase.handler.PdfHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mock.web.MockMultipartFile
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.String

@Service
class ProcessorScrapingDataService(
    private val prismaRepository: PrismaRepository,
    private val downloadPdfService: DownloadPdfService,
    private val utilitiesService: UtilitiesService,
    private val prismaExportService: PrismaExportService,
    private val logger: Logger = LoggerFactory.getLogger(ProcessorScrapingDataService::class.java)
) {

    fun execute(scrapingDobResponse: ScrapingDobResponse): ByteArray {

        val unableToFetchPdf: MutableList<Pair<String?, String?>> = mutableListOf()

        try {
            for (scholarResult in scrapingDobResponse.scholarResults!!) {

                // Verifica se há links de recursos disponíveis
                val pdfLink: Pair<JournalLinkSourceEnum, String> = if (!scholarResult.resources.isNullOrEmpty()) {
                    logger.info("Link de recurso encontrado, título: ${scholarResult.title}")
                    Pair(JournalLinkSourceEnum.RESOURCES_LINK, scholarResult.resources.first().link!!)
                } else {
                    logger.info("Nenhum link de recurso encontrado, título: ${scholarResult.title}")
                    unableToFetchPdf.add(Pair(scholarResult.title ?: "Sem título", scholarResult.titleLink))
                    Pair(JournalLinkSourceEnum.TITLE_LINK, scholarResult.titleLink!!)
                }

                // Tenta baixar o PDF
                val pdfFile = getPdfFile(pdfLink)

                var abstractText: String? = null
                var methodologyText: String? = null
                var methodologyCategory: String? = null

                if (pdfFile != null) {
                    //Salva o arquivo temporariamente para extração de texto
                    val tempFile = File.createTempFile("journalPDF-", ".pdf")
                    pdfFile.transferTo(tempFile)

                    // Extrai o texto do resumo e da metodologia
                    abstractText = utilitiesService.getAbstractTextFromPdf(tempFile)
                    methodologyText = utilitiesService.getMethodologyTextFromPdf(tempFile)
                    methodologyCategory = utilitiesService.categorizeMethodology(abstractText, methodologyText)

                    // Limpeza do arquivo temporário
                    tempFile.delete()
                }

                val prisma = Prisma(
                    source = SOURCE,
                    title = scholarResult.title ?: "Sem título",
                    type = scholarResult.type ?: "Sem tipo",
                    abstractText = abstractText ?: "Sem resumo",
                    methodologyText = methodologyText ?: "Sem metodologia",
                    methodologyCategory = methodologyCategory ?: "Sem classificação",
                    linkToJournal = pdfLink.second
                )
                prismaRepository.save(prisma)
            }

            //Loggar os títulos que não foi possível baixar o PDF
            unableToFetchPdf.forEachIndexed { index, item ->
                logger.info("${index}: Título: ${item.first}")
            }

            //Export da base de dado para Excel
            val exportFile = prismaExportService.exportToExcel()

            return exportFile //Retorna o arquivo Excel gerado
        } catch (e: Exception) {

            logger.error("Erro no processamento: ${e.message}")

            //Loggar os títulos que não foi possível baixar o PDF
            unableToFetchPdf.forEachIndexed { index, item ->
                logger.info("${index}: Título: ${item.first}")
            }

            //Export da base de dado para Excel
            val exportFile = prismaExportService.exportToExcel()

            return exportFile //Retorna o arquivo Excel gerado
        }
    }

    private fun getPdfFile(pdfLink: Pair<JournalLinkSourceEnum, String>): MockMultipartFile? {

        when (pdfLink.first) {
            JournalLinkSourceEnum.RESOURCES_LINK -> {
                logger.info("Baixando PDF do link de recursos: ${pdfLink.second}")
                return MockMultipartFile("journal.pdf", downloadPdfService.downloadPdf(pdfLink.second))
            }

            JournalLinkSourceEnum.TITLE_LINK -> {
                logger.info("Necessário acessar pagina do arquivo: ${pdfLink.second}")
                return null
            }
        }
    }

    companion object {
        const val SOURCE = "Google Acadêmico"
        const val PROCESSOR_ERROR = "ERRO DE PROCESSAMENTO"
    }
}

enum class JournalLinkSourceEnum() {
    RESOURCES_LINK,
    TITLE_LINK
}