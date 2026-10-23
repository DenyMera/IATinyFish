package com.apps.iatinyfish.presentation.deals

import com.apps.iatinyfish.data.model.ProductOffer
import com.apps.iatinyfish.data.repository.DealsRepository
import com.apps.iatinyfish.data.repository.RemoteSearchException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DealsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun validQueryPublishesResults() = runTest {
        val offer = sampleOffer()
        val fakeRepo = FakeDealsRepository(result = listOf(offer))
        val viewModel = DealsViewModel(fakeRepo)

        viewModel.onEvent(DealsEvent.QueryChanged("Switch OLED"))
        viewModel.onEvent(DealsEvent.SubmitSearch)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.hasSearched)
        assertEquals(listOf(offer), viewModel.state.value.offers)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun emptyQuerySetsErrorMessage() = runTest {
        val viewModel = DealsViewModel(FakeDealsRepository())

        viewModel.onEvent(DealsEvent.QueryChanged("   "))
        viewModel.onEvent(DealsEvent.SubmitSearch)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.hasSearched)
        assertTrue(viewModel.state.value.offers.isEmpty())
        assertEquals("Escribe un producto para iniciar la búsqueda.", viewModel.state.value.errorMessage)
    }

    @Test
    fun remoteErrorSetsErrorMessage() = runTest {
        val errorMessage = "Error de conexión al buscar en la web"
        val fakeRepo = FakeDealsRepository(shouldThrowError = true, errorMessage = errorMessage)
        val viewModel = DealsViewModel(fakeRepo)

        viewModel.onEvent(DealsEvent.QueryChanged("PlayStation 5"))
        viewModel.onEvent(DealsEvent.SubmitSearch)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.hasSearched)
        assertTrue(viewModel.state.value.offers.isEmpty())
        assertEquals(errorMessage, viewModel.state.value.errorMessage)
    }
}

private class FakeDealsRepository(
    override val isPreviewData: Boolean = false,
    private val result: List<ProductOffer> = emptyList(),
    private val shouldThrowError: Boolean = false,
    private val errorMessage: String = "Error simulado",
) : DealsRepository {
    override suspend fun search(query: String): List<ProductOffer> {
        if (shouldThrowError) {
            throw RemoteSearchException(errorMessage)
        }
        return result
    }
}

private fun sampleOffer() = ProductOffer(
    id = "1",
    store = "Tienda Ejemplo",
    productName = "Nintendo Switch OLED",
    price = "$349.99",
    originalPrice = "$399.99",
    shipping = "Envío Gratis",
    delivery = "Llega mañana",
    rating = "4.8",
    sourceUrl = "https://example.com/switch",
    summary = "Consola Nintendo Switch OLED en oferta.",
    tags = listOf("Oferta", "Videojuegos"),
    isBestValue = true,
)
