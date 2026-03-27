package com.scraping_journals.domain.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ScrapingDobResponse(
    val searchDetails: SearchDetails?,
    val profiles: Map<String, Any>?, // geralmente vazio
    val scholarResults: List<ScholarResult>?,
    val relatedSearches: List<Any>?,
    val pagination: Pagination?,
    val scrapingdogPagination: Pagination?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchDetails(
    val query: String?,
    val numberOfResults: String?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ScholarResult(
    val title: String?,
    val titleLink: String?,
    val id: String?,
    val type: String? = null,
    val displayedLink: String?,
    val snippet: String?,
    val authors: List<Author>?,
    val inlineLinks: InlineLinks?,
    val resources: List<Resource>? = emptyList()
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Author(
    val name: String?,
    val link: String?,
    val authorId: String?,
    val scrapingdogLink: String?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class InlineLinks(
    val versions: Versions? = null,
    val citedBy: CitedBy? = null,
    val relatedPagesLink: String? = null
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Versions(
    val total: String?,
    val link: String?,
    val clusterId: String?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CitedBy(
    val total: String?,
    val link: String?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Resource(
    val title: String?,
    val type: String?,
    val link: String?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Pagination(
    val current: Int?,
    val pageNo: Map<String, String>?
)