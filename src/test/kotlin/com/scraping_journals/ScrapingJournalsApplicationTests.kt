package com.scraping_journals

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ScrapingJournalsApplicationTests {

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

}
