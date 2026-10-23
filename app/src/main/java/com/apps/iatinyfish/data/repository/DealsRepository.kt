package com.apps.iatinyfish.data.repository

import com.apps.iatinyfish.data.model.ProductOffer

interface DealsRepository {
    val isPreviewData: Boolean
    suspend fun search(query: String): List<ProductOffer>
}

object PreviewDealsRepository : DealsRepository {
    override val isPreviewData: Boolean = true

    override suspend fun search(query: String): List<ProductOffer> {
        if (query.isBlank()) return emptyList()
        return listOf(
            ProductOffer(
                id = "1",
                store = "Tienda Ejemplo",
                productName = "Producto de muestra para '$query'",
                price = "$99.99",
                originalPrice = "$129.99",
                shipping = "Envío gratis",
                delivery = "Entrega en 24h",
                rating = "4.5",
                sourceUrl = "https://example.com",
                summary = "Oferta de prueba local para vista previa.",
                tags = listOf("Oferta", "Muestra"),
                isBestValue = true
            )
        )
    }
}
