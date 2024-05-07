/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.roctopus.data.source.network.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsApiResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ProductDetailsDto>,
)

@Serializable
data class ProductDetailsDto(
    @SerialName("code") val code: String,
    @SerialName("direction") val direction: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("description") val description: String,
    @SerialName("is_variable") val isVariable: Boolean,
    @SerialName("is_green") val isGreen: Boolean,
    @SerialName("is_tracker") val isTracker: Boolean,
    @SerialName("is_prepay") val isPrepay: Boolean,
    @SerialName("is_business") val isBusiness: Boolean,
    @SerialName("is_restricted") val isRestricted: Boolean,
    @SerialName("term") val term: Int?,
    @SerialName("available_from") val availableFrom: Instant,
    @SerialName("available_to") val availableTo: Instant?,
    @SerialName("links") val linkDtos: List<LinkDto>,
    @SerialName("brand") val brand: String,
)

@Serializable
data class LinkDto(
    @SerialName("href") val href: String,
    @SerialName("method") val method: String,
    @SerialName("rel") val rel: String,
)
