package com.apps.iatinyfish.presentation.deals

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.iatinyfish.data.model.ProductOffer

@Composable
fun DealsApp(state: DealsUiState, onEvent: (DealsEvent) -> Unit) {
    BackHandler(enabled = state.selectedOffer != null) {
        onEvent(DealsEvent.Back)
    }
    AnimatedContent(
        targetState = state.selectedOffer,
        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
        label = "screen",
    ) { offer ->
        if (offer == null) DealsSearchScreen(state, onEvent)
        else DealDetailScreen(offer, onBack = { onEvent(DealsEvent.Back) })
    }
}

@Composable
private fun DealsSearchScreen(state: DealsUiState, onEvent: (DealsEvent) -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FishMark()
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "TinyPrice",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Búsqueda web inteligente",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onEvent(DealsEvent.QueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("¿Qué quieres comparar?") },
                    leadingIcon = {
                        Text(
                            "⌕",
                            fontSize = 26.sp,
                            modifier = Modifier.semantics {
                                contentDescription = "Buscar productos"
                            },
                        )
                    },
                    trailingIcon = {
                        TextButton(onClick = { onEvent(DealsEvent.SubmitSearch) }) {
                            Text("Buscar", fontWeight = FontWeight.Bold)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { onEvent(DealsEvent.SubmitSearch) },
                    ),
                    shape = RoundedCornerShape(18.dp),
                )
            }
        },
    ) { padding ->
        if (state.hasSearched) {
            SearchResults(state, padding.calculateTopPadding(), onEvent)
        } else {
            DiscoveryHome(padding.calculateTopPadding()) {
                onEvent(DealsEvent.UseSuggestion(it))
            }
        }
    }
}

@Composable
private fun SearchResults(
    state: DealsUiState,
    topPadding: Dp,
    onEvent: (DealsEvent) -> Unit,
) {
    when {
        state.isLoading -> LoadingState(topPadding)
        state.offers.isEmpty() -> EmptyState(
            message = state.errorMessage ?: "No encontramos ofertas para “${state.query}”",
            topPadding = topPadding,
            onRetry = { onEvent(DealsEvent.SubmitSearch) },
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, topPadding + 6.dp, 20.dp, 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Resultados para “${state.query}”",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            items(state.offers, key = ProductOffer::id) { offer ->
                OfferCard(offer) { onEvent(DealsEvent.OpenOffer(offer)) }
            }
        }
    }
}

@Composable
private fun OfferCard(offer: ProductOffer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(
                        offer.store,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        offer.productName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    offer.price,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(offer.shipping, style = MaterialTheme.typography.bodySmall)
                Text("★ ${offer.rating}", fontWeight = FontWeight.Bold)
                Text(offer.delivery, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun DealDetailScreen(offer: ProductOffer, onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Volver", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                offer.store,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                offer.productName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                offer.price,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            if (!offer.originalPrice.isNullOrBlank()) {
                Text(
                    "Antes: ${offer.originalPrice}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Detalles de envío:", fontWeight = FontWeight.Bold)
                    Text("• ${offer.shipping}")
                    Text("• ${offer.delivery}")
                    Text("• Calificación: ★ ${offer.rating}")
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                offer.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    if (offer.sourceUrl.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.sourceUrl))
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ver en la tienda", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun FishMark() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("🐟", fontSize = 22.sp)
    }
}

@Composable
private fun DiscoveryHome(topPadding: Dp, onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf("Nintendo Switch OLED", "PlayStation 5", "Laptop Gamer", "Audífonos Bluetooth")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text("🔍", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Encuentra las mejores ofertas web",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "Busca cualquier producto para comparar precios en tiempo real",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.take(2).forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = { Text(suggestion) }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.drop(2).forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = { Text(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun LoadingState(topPadding: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Buscando las mejores ofertas...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun EmptyState(message: String, topPadding: Dp, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏷️", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}
