package com.deymervilla.network.parser

import com.deymervilla.network.constants.NetworkConstants.DEFAULTS.COMMA_SEPARATOR
import com.deymervilla.network.constants.NetworkConstants.DEFAULTS.DEFAULT_EMPTY
import com.deymervilla.network.constants.NetworkConstants.DEFAULTS.SYMBOL_PRICE
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_INITIAL_DATA
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_ITEMS
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_ITEM_STACKS
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_ITEM
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_PAGE_PROPS
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_PROPS
import com.deymervilla.network.constants.NetworkConstants.JSON_PATH.FIELD_SEARCH_RESULT
import com.deymervilla.network.dto.ProductDTO
import com.deymervilla.network.dto.RawItemDTO
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * Navigate the raw response from Axesso (nested under
 * item.props.pageProps.initialData.searchResult.itemStacks[0].items)
 * and flatten it into a straightforward List<ProductDTO>.
 *
 * It only keeps the first item stack (the main search results); secondary stacks
 * like "highly rated by customers" are ignored. It also filters out items that
 * are not __typename == "Product" (e.g., AdPlaceholder).
 */
class SearchResultDeserializer: JsonDeserializer<List<ProductDTO>> {

    private val rawItemGson = Gson()

    companion object {
        private const val FIELD_TYPENAME = "__typename"
        private const val PRODUCT_TYPENAME = "Product"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<ProductDTO> {
        val root = json?.asJsonObject ?: return emptyList()

        val items = root
            .getAsJsonObjectOrNull(FIELD_ITEM)
            ?.getAsJsonObjectOrNull(FIELD_PROPS)
            ?.getAsJsonObjectOrNull(FIELD_PAGE_PROPS)
            ?.getAsJsonObjectOrNull(FIELD_INITIAL_DATA)
            ?.getAsJsonObjectOrNull(FIELD_SEARCH_RESULT)
            ?.getAsJsonArrayOrNull(FIELD_ITEM_STACKS)
            ?.firstOrNull { it.isJsonObject }
            ?.asJsonObject
            ?.getAsJsonArrayOrNull(FIELD_ITEMS)
            ?: return emptyList()

        return items.mapNotNull { element ->
            runCatching {
                if (!element.isJsonObject) return@runCatching null
                val typeName = element.asJsonObject.get(FIELD_TYPENAME)?.asString
                if (typeName != PRODUCT_TYPENAME) return@runCatching null

                val raw = rawItemGson.fromJson(element, RawItemDTO::class.java)
                raw?.takeIf { !it.usItemId.isNullOrBlank() }?.toProductDTO()
            }.getOrNull()
        }
    }

    private fun RawItemDTO.toProductDTO(): ProductDTO {
        return ProductDTO(
            productId = usItemId,
            title = name,
            price = resolvePrice(),
            wasPrice = resolveWasPrice(),
            thumbnailUrl = image,
            averageRating = averageRating,
            numberOfReviews = numberOfReviews,
            sellerName = sellerName,
            canonicalUrl = canonicalUrl,
            category = departmentName,
            description = shortDescription,
            isOutOfStock = isOutOfStock ?: false
        )
    }

    private fun RawItemDTO.resolvePrice(): Double? {
        val displayPrice = priceInfo?.linePriceDisplay
            ?.replace(SYMBOL_PRICE, DEFAULT_EMPTY)
            ?.replace(COMMA_SEPARATOR, DEFAULT_EMPTY)
            ?.trim()
            ?.toDoubleOrNull()
        return displayPrice ?: price
    }

    private fun RawItemDTO.resolveWasPrice(): Double? {
        return priceInfo?.wasPrice
            ?.replace(SYMBOL_PRICE, DEFAULT_EMPTY)
            ?.replace(COMMA_SEPARATOR, DEFAULT_EMPTY)
            ?.trim()
            ?.toDoubleOrNull()
    }

    private fun JsonObject.getAsJsonObjectOrNull(field: String): JsonObject? =
        if (has(field) && get(field).isJsonObject) getAsJsonObject(field) else null

    private fun JsonObject.getAsJsonArrayOrNull(field: String) =
        if (has(field) && get(field).isJsonArray) getAsJsonArray(field) else null
}