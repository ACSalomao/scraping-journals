package com.scraping_journals.usecase.handler

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Component
import java.io.File

@Component
class PdfHandler {

    fun extractAbstract(path: String): String? {
        PDDocument.load(File(path)).use { document ->
            val stripper = PDFTextStripper()
            stripper.startPage = 1
            stripper.endPage = document.numberOfPages
            val texto = stripper.getText(document)

            // (opcional) — depuração: salvar o texto lido
//            File("debug_texto_extraido.txt").writeText(texto)

            // Regex mais tolerante: aceita várias quebras de linha e formatações
            val regex = Regex(
                pattern = "(?i)(abstract)\\s*[:\\-–\\s]*([\\s\\S]{100,2000}?)(?=\\n\\s*(introduction|1\\.|keywords|resumo|sumário|background|materials|methods)\\b)",
                options = setOf(RegexOption.IGNORE_CASE)
            )

            val match = regex.find(texto)
            val abstract = match?.groups?.get(2)?.value?.trim()

            return abstract?.replace("\\s+".toRegex(), " ")
        }
    }

    fun extractAbstractBr(path: String): String? {
        PDDocument.load(File(path)).use { document ->
            val stripper = PDFTextStripper()
            stripper.startPage = 1
            stripper.endPage = document.numberOfPages
            val texto = stripper.getText(document)

            // (opcional) salvar o texto para debug
//            File("debug_texto_extraido.txt").writeText(texto)

            // Regex que captura o texto entre "Resumo" e "Palavras-chave" ou "Introdução"
            val regex = Regex(
                pattern = "(?i)(resumo)\\s*[:\\-–\\s]*([\\s\\S]{100,3000}?)(?=\\n\\s*(palavras-chave|keywords|abstract|introdução|1\\.|sumário)\\b)",
                options = setOf(RegexOption.IGNORE_CASE)
            )

            val match = regex.find(texto)
            val resumo = match?.groups?.get(2)?.value?.trim()

            return resumo?.replace("\\s+".toRegex(), " ")
        }
    }

    fun extractMethodologyBr(caminhoPdf: String): String? {
        PDDocument.load(File(caminhoPdf)).use { document ->
            val stripper = PDFTextStripper()
            stripper.startPage = 1
            stripper.endPage = document.numberOfPages
            val texto = stripper.getText(document)

            // (opcional) salvar texto para inspecionar o conteúdo real
//            File("debug_texto_extraido.txt").writeText(texto)

            // Expressão regular que captura o conteúdo da seção "Metodologia", "Método" ou "Methodology"
            val regex = Regex(
                pattern = "(?i)(metodologia|metodologia e procedimentos|método|methods|methodology)\\s*[:\\-–\\s]*([\\s\\S]{200,6000}?)(?=\\n\\s*(resultados|results|discussão|discussion|análise|analysis|conclusão|conclusion)\\b)",
                options = setOf(RegexOption.IGNORE_CASE)
            )

            val match = regex.find(texto)
            val metodologia = match?.groups?.get(2)?.value?.trim()

            return metodologia?.replace("\\s+".toRegex(), " ")
        }
    }
}