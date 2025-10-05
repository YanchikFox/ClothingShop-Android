package com.shop.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shop.app.data.model.Product
import com.shop.app.data.model.ProductFeature
import com.shop.app.data.model.ProductReview
import com.shop.app.di.ServiceLocator
import com.shop.app.ui.theme.TShopAppTheme
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    productId: String?,
    products: List<Product>,
    modifier: Modifier = Modifier,
    onAddToCartClick: (product: Product, quantity: Int) -> Unit
) {
    val product = products.firstOrNull { it.id == productId }
    var quantity by remember { mutableStateOf(1) }

    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Product not found")
        }
        return
    }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("uk", "UA")).apply {
            maximumFractionDigits = 0
        }
    }

    val galleryImages = remember(product.imageUrls, product.imagePath) {
        if (product.imageUrls.isNotEmpty()) product.imageUrls else listOfNotNull(product.imagePath)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProductImageGallery(
            imageUrls = galleryImages,
            contentDescription = product.name
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = currencyFormat.format(product.price),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }

        DetailSection(title = "Состав") {
            Text(text = product.composition, style = MaterialTheme.typography.bodyMedium)
        }

        DetailSection(title = "Уход") {
            Text(text = product.careInstructions, style = MaterialTheme.typography.bodyMedium)
        }

        if (product.features.isNotEmpty()) {
            DetailSection(title = "Характеристики") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.features.forEachIndexed { index, feature ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = feature.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = feature.value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (index < product.features.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        if (product.reviews.isNotEmpty()) {
            DetailSection(title = "Отзывы") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    product.reviews.forEach { review ->
                        ReviewCard(review = review)
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            QuantitySelector(
                quantity = quantity,
                onIncrement = { quantity++ },
                onDecrement = { if (quantity > 1) quantity-- }
            )
            Button(
                onClick = { onAddToCartClick(product, quantity) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Добавить в корзину")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductImageGallery(
    imageUrls: List<String>,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size.coerceAtLeast(1) })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) { page ->
            val imagePath = imageUrls.getOrNull(page)
            ZoomableProductImage(
                imagePath = imagePath,
                contentDescription = contentDescription
            )
        }

        if (imageUrls.size > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                imageUrls.forEachIndexed { index, _ ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                }
                            )
                            .pointerInput(pagerState.pageCount) {
                                detectTapGestures {
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                }
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ZoomableProductImage(
    imagePath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 3f)
        if (newScale == 1f) {
            offset = Offset.Zero
        } else {
            val limitedOffset = offset + panChange
            val maxOffset = 400f
            offset = Offset(
                x = limitedOffset.x.coerceIn(-maxOffset, maxOffset),
                y = limitedOffset.y.coerceIn(-maxOffset, maxOffset)
            )
        }
        scale = newScale
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .transformable(transformableState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2f
                        if (scale == 1f) {
                            offset = Offset.Zero
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (imagePath != null) {
            AsyncImage(
                model = ServiceLocator.imagesBaseUrl + imagePath,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "Нет изображения",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Column(content = content)
    }
}

@Composable
private fun ReviewCard(review: ProductReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${review.rating}/5",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onDecrement, enabled = quantity > 1) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
        }
        Text(
            text = "$quantity",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.width(48.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onIncrement) {
            Icon(Icons.Default.Add, contentDescription = "Increase quantity")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    val sampleProduct = Product(
        id = "su001",
        article = "1023",
        categoryId = "unisex",
        name = "Embroidered T-shirt",
        description = "Complete product description for preview...",
        price = 1200.0,
        priceString = "1 200 ₴",
        isBestseller = true,
        imageUrls = listOf("images/1.jpg", "images/1_detail.jpg"),
        composition = "100% organic cotton",
        careInstructions = "Wash at 30°C, do not bleach, iron at medium temperature.",
        features = listOf(
            ProductFeature("Fit", "Relaxed"),
            ProductFeature("Made in", "Portugal")
        ),
        reviews = listOf(
            ProductReview("Iryna", 5, "Очень мягкая ткань"),
            ProductReview("Olena", 4, "Отлично сидит")
        )
    )

    TShopAppTheme {
        ProductDetailScreen(
            productId = sampleProduct.id,
            products = listOf(sampleProduct),
            onAddToCartClick = { _, _ -> }
        )
    }
}