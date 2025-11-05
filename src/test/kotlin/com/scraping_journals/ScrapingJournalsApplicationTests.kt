package com.scraping_journals

import com.scraping_journals.usecase.handler.PdfHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ScrapingJournalsApplicationTests(){
    @Autowired
    lateinit var pdfHandler: PdfHandler
	@Test
	fun contextLoads() {

        val list1 = listOf("a", "b", "c")
        val list2 = listOf("d", "e", "f")
        val listComplete = mutableListOf<String>()

        println("ListComplete: $listComplete")
        listComplete.addAll(list2)

        println("ListComplete: $listComplete")

        listComplete.addAll(list1)
         println("ListComplete: $listComplete")

        listComplete.addAll(emptyList())

        println("ListComplete: $listComplete")
	}

    @Test
	fun pdfReadAndGetAbstractTest() {
        val abstract = pdfHandler.extractAbstract("/home/asalomao/Downloads/pdfFileTest.pdf")

        println(abstract)
    }


    @Test
    fun iterationTest(){
        val list = listOf("A", "B", "C")

        list.forEachIndexed { index, item ->
            println("Index: $index, Item: $item")
        }

    }

}
