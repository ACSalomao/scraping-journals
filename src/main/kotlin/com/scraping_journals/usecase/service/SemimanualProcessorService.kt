package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.repository.PrismaRepository
import com.scraping_journals.domain.Prisma
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class SemimanualProcessorService(
    private val utilitiesService: UtilitiesService,
    private val prismaRepository: PrismaRepository
) {

    fun semimanualExecute(source: String, title: String, type: String?, file: MultipartFile) {

        val tempFile = File.createTempFile("myFile-", ".pdf")
        file.transferTo(tempFile)

        val abstractText = utilitiesService.getAbstractTextFromPdf(tempFile)
        val methodologyText = utilitiesService.getMethodologyTextFromPdf(tempFile)
        val methodologyCategory = utilitiesService.categorizeMethodology(abstractText, methodologyText)
        tempFile.delete()

        val prisma = Prisma(
            source = source,
            title = title,
            type = type ?: "Sem tipo",
            abstractText =  abstractText ?: "Sem resumo",
            methodologyText = methodologyText ?: "Sem metodologia",
            methodologyCategory = methodologyCategory,
            linkToJournal = "Sem link"
        )

        prismaRepository.save(prisma)
    }
}