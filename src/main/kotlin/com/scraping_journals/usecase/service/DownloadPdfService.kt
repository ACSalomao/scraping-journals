package com.scraping_journals.usecase.service

import com.scraping_journals.adapter.client.UnsafeWebClientFactory
import org.springframework.stereotype.Service
import java.net.URI

@Service
class DownloadPdfService(
    private val unsafeWebClientFactory: UnsafeWebClientFactory
) {

    fun downloadPdf(url: String): ByteArray? {
        val webClient = unsafeWebClientFactory.create()

        try {


            val result = webClient.get()
            .uri(URI(url))
//                .accept(MediaType.APPLICATION_PDF)
                .retrieve()
                .bodyToMono(ByteArray::class.java)
                .block() ?: throw RuntimeException("Erro ao baixar PDF")

//            Persiste o arquivo PDF localmente
//            File("/home/asalomao/downloaded_file.pdf").writeBytes(result)
            return result
        }catch (e: Exception) {
            println("MESSAGE: ${e.message}")
            println("CAUSE: ${e.cause}")
            return null
        }

    }
}