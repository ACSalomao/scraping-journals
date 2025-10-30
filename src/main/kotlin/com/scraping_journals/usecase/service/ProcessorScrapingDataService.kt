package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.repository.PrismaRepository
import com.scraping_journals.domain.response.ScrapingDobResponse

class ProcessorScrapingDataService(
    private val prismaRepository: PrismaRepository
) {

    fun execute(scrapingDobResponse: ScrapingDobResponse) {

        for (scholarResult in scrapingDobResponse.scholarResults!!){
            val pdfLink: String

            if (!scholarResult.resources.isNullOrEmpty()){
                pdfLink = scholarResult.resources.first().link!!
            }else{
                pdfLink =
            }

            getPdfFile(pdfLink)
        }
        return
    }

    private fun getPdfFile(pdfLink: String) {


    }
}