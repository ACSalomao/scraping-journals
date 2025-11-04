package com.scraping_journals.usecase.service


import com.scraping_journals.adapter.repository.PrismaRepository
import com.scraping_journals.domain.Prisma
import com.scraping_journals.domain.response.ScrapingDobResponse
import com.scraping_journals.usecase.handler.PdfHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mock.web.MockMultipartFile
import org.springframework.stereotype.Service
import java.io.File
import kotlin.String

@Service
class ProcessorScrapingDataService(
    private val prismaRepository: PrismaRepository,
    private val downloadPdfService: DownloadPdfService,
    private val pdfHandler: PdfHandler,
    private val logger: Logger = LoggerFactory.getLogger(ProcessorScrapingDataService::class.java)
) {

    fun execute(scrapingDobResponse: ScrapingDobResponse) {

        val unableToFetchPdf: MutableList<Pair<String?, String?>> = mutableListOf()
        for (scholarResult in scrapingDobResponse.scholarResults!!) {

            // Verifica se há links de recursos disponíveis
            val pdfLink: Pair<JournalLinkSourceEnum, String> = if (!scholarResult.resources.isNullOrEmpty()) {
                logger.info("Link de recurso encontrado, título: ${scholarResult.title}")
                Pair(JournalLinkSourceEnum.RESOURCES_LINK, scholarResult.resources.first().link!!)
            } else {
                logger.info("Nenhum link de recurso encontrado, título: ${scholarResult.title}")
                unableToFetchPdf.add(Pair(scholarResult.title, scholarResult.titleLink))
                Pair(JournalLinkSourceEnum.TITLE_LINK, scholarResult.titleLink!!)
            }

            // Tenta baixar o PDF
            val pdfFile = getPdfFile(pdfLink)

            //Salva o arquivo temporariamente para extração de texto
            val tempFile = File.createTempFile("uploaded-", ".pdf")
            pdfFile?.transferTo(tempFile)

            // Extrai o texto do resumo e da metodologia
            val abstractText = getAbstractTextFromPdf(tempFile)
            val methodologyText = getMethodologyTextFromPdf(tempFile)
            val methodologyCategory = categorizeMethodology(abstractText, methodologyText)

            tempFile.delete() // Limpeza do arquivo temporário
            val prisma = Prisma(
                source = SOURCE,
                title = scholarResult.title!!,
                abstractText =,
                methodologyText =,
                methodologyCategory = methodologyCategory,
                linkToJournal = pdfLink.second
            )
            prismaRepository.save(prisma)
        }

        //Export da base de dado para Excel

        //Loggar os títulos que não foi possível baixar o PDF

        return  //Retorna o arquivo Excel gerado
    }


    private fun getAbstractTextFromPdf(tempFile: File): String?
         = pdfHandler.extractAbstractBr(tempFile.path)


    private fun getMethodologyTextFromPdf(tempFile: File)
        = pdfHandler.extractMethodologyBr(tempFile.path)

    private fun categorizeMethodology(abstractText: String?, methodologyText: String?): String? {
        TODO("Not yet implemented")
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

        return null
    }

    companion object {
        const val SOURCE = "Google Acadêmico"
        const val DIRECT_LINK = "DIRECT_LINK"
        const val NOT_DIRECT_LINK = "NOT_DIRECT_LINK"
    }
}

enum class JournalLinkSourceEnum() {
    RESOURCES_LINK,
    TITLE_LINK
}