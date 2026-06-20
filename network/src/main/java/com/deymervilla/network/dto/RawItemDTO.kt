package com.deymervilla.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Internal mirror of the raw Axesso/Walmart response, used only by WalmartSearchResultDeserializer
 * to navigate the JSON and extract the products.
 *
 * This class is NEVER exposed outside the network module: ApiService directly returns
 * List<ProductDTO> thanks to the TypeAdapter registered in Gson.
 */
internal data class RawItemDTO(
    @SerializedName("usItemId")
    val usItemId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("priceInfo")
    val priceInfo: RawPriceInfoDTO?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("averageRating")
    val averageRating: Double?,
    @SerializedName("numberOfReviews")
    val numberOfReviews: Int?,
    @SerializedName("sellerName")
    val sellerName: String?,
    @SerializedName("canonicalUrl")
    val canonicalUrl: String?,
    @SerializedName("departmentName")
    val departmentName: String?,
    @SerializedName("shortDescription")
    val shortDescription: String?,
    @SerializedName("isOutOfStock")
    val isOutOfStock: Boolean?
)

internal data class RawPriceInfoDTO(
    @SerializedName("linePriceDisplay")
    val linePriceDisplay: String?,
    @SerializedName("wasPrice")
    val wasPrice: String?
)