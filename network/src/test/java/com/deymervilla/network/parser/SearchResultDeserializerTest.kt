package com.deymervilla.network.parser

import com.deymervilla.network.dto.ProductDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchResultDeserializerTest {

    private lateinit var gson: Gson
    private val productListType = object : TypeToken<List<ProductDTO>>() {}.type

    @Before
    fun setUp() {
        gson = GsonBuilder()
            .registerTypeAdapter(productListType, SearchResultDeserializer())
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

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertEquals(1, result.size)
        with(result.first()) {
            Assert.assertEquals("39443659", productId)
            Assert.assertEquals("Sony MDR-ZX110 Wired On-Ear Headphones, Black", title)
            Assert.assertEquals(14.88, price!!, 0.001)
            Assert.assertEquals("Walmart.com", sellerName)
        }
    }

    @Test
    fun `deserialize falls back to truncated price when linePriceDisplay is blank`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "usItemId": "310157752",
                    "name": "Sony WH-1000XM4",
                    "price": 398,
                    "priceInfo": { "linePriceDisplay": "" },
                    "image": "https://i5.walmartimages.com/seo/image2.jpeg",
                    "averageRating": 4.3,
                    "numberOfReviews": 1975,
                    "sellerName": "Beach Camera",
                    "canonicalUrl": "/ip/Sony-WH-1000XM4/310157752"
                  }
                ]
            """.trimIndent()
        )

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertEquals(1, result.size)
        Assert.assertEquals(398.0, result.first().price!!, 0.001)
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

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertEquals(1, result.size)
        Assert.assertEquals("39443659", result.first().productId)
    }

    @Test
    fun `deserialize ignores items without usItemId`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "name": "Product without id",
                    "price": 14
                  }
                ]
            """.trimIndent()
        )

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `deserialize returns empty list when itemStacks is missing`() {
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

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `deserialize returns empty list for completely malformed root`() {
        val json = """{ "unexpected": "shape" }"""

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertTrue(result.isEmpty())
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

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        Assert.assertEquals(1, result.size)
        Assert.assertEquals("main-stack-item", result.first().productId)
    }

    @Test
    fun `deserialize parses wasPrice when discount is present`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "usItemId": "2809072158",
                    "name": "Sony WH-CH720N Headphones",
                    "price": 99,
                    "priceInfo": {
                      "linePriceDisplay": "${'$'}99.99",
                      "wasPrice": "${'$'}179.99"
                    },
                    "image": "https://i5.walmartimages.com/seo/image2.jpeg",
                    "averageRating": 4.5,
                    "numberOfReviews": 11,
                    "sellerName": "Walmart.com",
                    "canonicalUrl": "/ip/Sony-WH-CH720N/2809072158",
                    "departmentName": "Electronics",
                    "shortDescription": "Wireless headphones with noise cancelling",
                    "isOutOfStock": false
                  }
                ]
            """.trimIndent()
        )

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        assertEquals(1, result.size)
        with(result.first()) {
            assertEquals(99.99, price!!, 0.001)
            assertEquals(179.99, wasPrice!!, 0.001)
            assertEquals("Electronics", category)
            assertEquals(false, isOutOfStock)
        }
    }

    @Test
    fun `deserialize sets wasPrice null when no discount is present`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "usItemId": "39443659",
                    "name": "Sony MDR-ZX110",
                    "price": 14,
                    "priceInfo": { "linePriceDisplay": "${'$'}14.88", "wasPrice": "" },
                    "image": "img.jpeg",
                    "departmentName": "Electronics",
                    "isOutOfStock": false
                  }
                ]
            """.trimIndent()
        )

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        assertEquals(1, result.size)
        assertNull(result.first().wasPrice)
    }

    @Test
    fun `deserialize defaults isOutOfStock to false when missing`() {
        val json = validResponseJson(
            items = """
                [
                  {
                    "__typename": "Product",
                    "usItemId": "39443659",
                    "name": "Sony MDR-ZX110",
                    "price": 14
                  }
                ]
            """.trimIndent()
        )

        val result: List<ProductDTO> = gson.fromJson(json, productListType)

        assertEquals(1, result.size)
        assertEquals(false, result.first().isOutOfStock)
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