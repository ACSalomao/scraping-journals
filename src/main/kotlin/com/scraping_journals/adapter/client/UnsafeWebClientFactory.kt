package com.scraping_journals.adapter.client

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Service
class UnsafeWebClientFactory {


    fun create(): WebClient {
        // Configura SSL para confiar em todos os certificados
        val sslContext = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()

        // HttpClient do Reactor Netty com SSL desabilitado
        val httpClient = HttpClient.create()
            .secure { spec -> spec.sslContext(sslContext) }

        // Cria WebClient usando o HttpClient
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }


}