package com.scraping_journals.usecase.service

import com.scraping_journals.usecase.handler.PdfHandler
import com.scraping_journals.usecase.service.ProcessorScrapingDataService.Companion.PROCESSOR_ERROR
import org.springframework.stereotype.Service
import java.io.File

@Service
class UtilitiesService(
    private val pdfHandler: PdfHandler,
    private val openRouterService: OpenRouterService,
    private val prismaExportService: PrismaExportService
) {

    fun generatePrismaXlsx(): ByteArray {
        return prismaExportService.exportToExcel()
    }

    fun getAbstractTextFromPdf(tempFile: File): String? = pdfHandler.extractAbstractBr(tempFile.path)


    fun getMethodologyTextFromPdf(tempFile: File) = pdfHandler.extractMethodologyBr(tempFile.path)

    fun categorizeMethodology(abstractText: String?, methodologyText: String?): String {
        if (abstractText == null && methodologyText == null) {
            return PROCESSOR_ERROR
        }

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
}