package com.deymervilla.network.parser

import com.deymervilla.network.dto.response.SearchResponseDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class SearchResultDeserializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder()
            .registerTypeAdapter(SearchResponseDTO::class.java, SearchResultDeserializer())
            .create()
    }

    @Test
    fun `deserialize extracts products from nested itemStacks structure`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "usItemId": "39443659",
                    "name": "Sony MDR-ZX110 Wired On-Ear Headphones, Black",
                    "price": 14,
                    "priceInfo": { "linePriceDisplay": "${'$'}14.88" },
                    "image": "https://i5.walmartimages.com/seo/image1.jpeg",
                    "averageRating": 4.4,
                    "numberOfReviews": 2553,
                    "sellerName": "Walmart.com",
                    "canonicalUrl": "/ip/Sony-MDR-ZX110/39443659"
                  }
                ]
            """.trimIndent()
        )

        val result = gson.fromJson(json, SearchResponseDTO::class.java)

        assertEquals(1, result.products.size)
        with(result.products.first()) {
            assertEquals("39443659", productId)
            assertEquals("Sony MDR-ZX110 Wired On-Ear Headphones, Black", title)
            assertEquals(14.88, price!!, 0.001)
            assertEquals("Walmart.com", sellerName)
        }
    }

    @Test
    fun `deserialize ignores non-Product typename items like AdPlaceholder`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "usItemId": "39443659",
                    "name": "Sony Product",
                    "price": 14,
                    "priceInfo": { "linePriceDisplay": "${'$'}14.88" },
                    "image": "img.jpeg",
                    "averageRating": 4.4,
                    "numberOfReviews": 100,
                    "sellerName": "Walmart.com",
                    "canonicalUrl": "/ip/x"
                  },
                  {
                    "__typename": "AdPlaceholder",
                    "adUuid": "72210d51-c0f6-4c7c-ac21-a50459cbf3e6",
                    "hasAd": false
                  }
                ]
            """.trimIndent()
        )

        val result = gson.fromJson(json, SearchResponseDTO::class.java)

        assertEquals(1, result.products.size)
        assertEquals("39443659", result.products.first().productId)
    }

    @Test
    fun `deserialize returns empty products list when itemStacks is missing`() {
        val json = """
            {
              "item": {
                "props": {
                  "pageProps": {
                    "initialData": {
                      "searchResult": {}
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val result = gson.fromJson(json, SearchResponseDTO::class.java)

        assertTrue(result.products.isEmpty())
    }

    @Test
    fun `deserialize returns empty products list for completely malformed root`() {
        val json = """{ "unexpected": "shape" }"""

        val result = gson.fromJson(json, SearchResponseDTO::class.java)

        assertTrue(result.products.isEmpty())
    }

    @Test
    fun `deserialize only reads the first itemStack ignoring secondary stacks`() {
        val json = """
            {
              "item": {
                "props": {
                  "pageProps": {
                    "initialData": {
                      "searchResult": {
                        "itemStacks": [
                          {
                            "items": [
                              {
                                "__typename": "Product",
                                "usItemId": "main-stack-item",
                                "name": "Main result",
                                "price": 10
                              }
                            ]
                          },
                          {
                            "items": [
                              {
                                "__typename": "Product",
                                "usItemId": "highly-rated-item",
                                "name": "Highly rated carousel item",
                                "price": 20
                              }
                            ]
                          }
                        ]
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val result = gson.fromJson(json, SearchResponseDTO::class.java)

        assertEquals(1, result.products.size)
        assertEquals("main-stack-item", result.products.first().productId)
    }

    private fun validResponseJson(items: String): String = """
        {
          "responseStatus": "PRODUCT_FOUND_RESPONSE",
          "item": {
            "props": {
              "pageProps": {
                "initialData": {
                  "searchResult": {
                    "itemStacks": [
                      {
                        "items": $items
                      }
                    ]
                  }
                }
              }
            }
          }
        }
    """.trimIndent()
}