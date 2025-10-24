package com.scraping_journals

import com.scraping_journals.adapter.client.ScrapingDogClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = [ScrapingDogClient::class])
class ScrapingJournalsApplication

fun main(args: Array<String>) {
	runApplication<ScrapingJournalsApplication>(*args)
}
