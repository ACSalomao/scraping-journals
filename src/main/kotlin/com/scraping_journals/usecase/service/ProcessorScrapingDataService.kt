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
    private val openRouterService: OpenRouterService,
    private val prismaExportService: PrismaExportService,
    private val logger: Logger = LoggerFactory.getLogger(ProcessorScrapingDataService::class.java)
) {

    fun execute(scrapingDobResponse: ScrapingDobResponse): ByteArray {

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

            var abstractText: String? = null
            var methodologyText: String? = null
            var methodologyCategory: String? = null

            if (pdfFile != null) {
                //Salva o arquivo temporariamente para extração de texto
                val tempFile = File.createTempFile("journalPDF", ".pdf")
                pdfFile.transferTo(tempFile)

                // Extrai o texto do resumo e da metodologia
                abstractText = getAbstractTextFromPdf(tempFile)
                methodologyText = getMethodologyTextFromPdf(tempFile)
                methodologyCategory = categorizeMethodology(abstractText, methodologyText)

                // Limpeza do arquivo temporário
                tempFile.delete()
            }

            val prisma = Prisma(
                source = SOURCE,
                title = scholarResult.title!!,
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
    }


    private fun getAbstractTextFromPdf(tempFile: File): String? = pdfHandler.extractAbstractBr(tempFile.path)


    private fun getMethodologyTextFromPdf(tempFile: File) = pdfHandler.extractMethodologyBr(tempFile.path)

    private fun categorizeMethodology(abstractText: String?, methodologyText: String?): String? {

        val contextStart =
            "Dado os textos abaixo, classifique o trabalho de acordo com o tipo de artigo mais apropriado do array fornecido."
        val typesOfArticles =
            "\nTipos de Artigo: [\"Adaptive Clinical Trial\", \"Address\", \"Autobiography\", \"Bibliography\", \"Biography\", \"Books and Documents\", \"Case Reports\", \"Classical Article\", \"Clinical Conference\", \"Clinical Study\", \"Clinical Trial\", \"Clinical Trial Protocol\", \"Clinical Trial, Phase I\", \"Clinical Trial, Phase II\", \"Clinical Trial, Phase III\", \"Clinical Trial, Phase IV\", \"Clinical Trial, Veterinary\", \"Collected Work\", \"Comment\", \"Comparative Study\", \"Congress\", \"Consensus Development Conference\", \"Consensus Development Conference, NIH\", \"Controlled Clinical Trial\", \"Corrected and Republished Article\", \"Dataset\", \"Dictionary\", \"Directory\", \"Duplicate Publication\", \"Editorial\", \"Electronic Supplementary Materials\", \"English Abstract\", \"Equivalence Trial\", \"Evaluation Study\", \"Expression of Concern\", \"Festschrift\", \"Government Publication\", \"Guideline\", \"Historical Article\", \"Interactive Tutorial\", \"Interview\", \"Introductory Journal Article\", \"Lecture\", \"Legal Case\", \"Legislation\", \"Letter\", \"Meta-Analysis\", \"Multicenter Study\", \"Network Meta-Analysis\", \"News\", \"Newspaper Article\", \"Observational Study\", \"Observational Study, Veterinary\", \"Overall\", \"Patient Education Handout\", \"Periodical Index\", \"Personal Narrative\", \"Portrait\", \"Practice Guideline\", \"Pragmatic Clinical Trial\", \"Preprint\", \"Published Erratum\", \"Randomized Controlled Trial\", \"Randomized Controlled Trial, Veterinary\", \"Research Support, American Recovery and Reinvestment Act\", \"Research Support, N.I.H., Extramural\", \"Research Support, N.I.H., Intramural\", \"Research Support, Non-U.S. Gov't\", \"Research Support, U.S. Gov't, Non-P.H.S.\", \"Research Support, U.S. Gov't, P.H.S.\", \"Research Support, U.S. Gov't\", \"Retracted Publication\", \"Retraction of Publication\", \"Review\", \"Scientific Integrity Review\", \"Scoping Review\", \"Systematic Review\", \"Technical Report\", \"Twin Study\", \"Validation Study\", \"Video-Audio Media\", \"Webcast\"]"
        val contextEnd = "\nRetorne apenas o tipo de artigo e nada mais."

        val query = StringBuilder()
            .append(contextStart)
            .append(typesOfArticles)
            .append("\nResumo: \n").append(abstractText)
            .append("\nMetodologia: \n").append(methodologyText)
            .append(contextEnd)
            .toString()

//        logger.info("Query: $query")

        val typeOfArticle = openRouterService.ask(query)

//        logger.info("OpenRouter Response: $typeOfArticle")

        return typeOfArticle
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
        const val DIRECT_LINK = "DIRECT_LINK"
        const val NOT_DIRECT_LINK = "NOT_DIRECT_LINK"
    }
}

enum class JournalLinkSourceEnum() {
    RESOURCES_LINK,
    TITLE_LINK
}