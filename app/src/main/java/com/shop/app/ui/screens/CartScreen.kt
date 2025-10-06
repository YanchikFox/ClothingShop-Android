package com.shop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.data.model.ProductFeature
import com.shop.app.data.model.ProductReview
import com.shop.app.ui.components.CartItemRow
import com.shop.app.ui.theme.TShopAppTheme
import com.shop.app.R

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    totalPrice: Double,
    modifier: Modifier = Modifier,
    formatPrice: (Double) -> String,
    imagesBaseUrl: String,
    onRemoveClick: (String) -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
) {
    if (cartItems.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.cart_empty_message))
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemRow(
                        cartItem = cartItem,
                        formatPrice = formatPrice,
                        imagesBaseUrl = imagesBaseUrl,
                        onRemoveClick = { onRemoveClick(cartItem.product.id) },
                        onIncrement = { onIncrement(cartItem.product.id) },
                        onDecrement = { onDecrement(cartItem.product.id) }
                    )
                    HorizontalDivider()
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                // Display calculated total price
                Text(
                    text = stringResource(
                        R.string.cart_total_amount,
                        formatPrice(totalPrice)
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.cart_checkout_placeholder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Preview data needs to be consistent with new model
@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    val sampleProduct = Product(
        id = "su001",
        article = "1023",
        categoryId = "unisex",
        name = "Sample Product",
        description = "Description",
        price = 1200.0,
        priceString = "1 200 ₴",
        isBestseller = true,
        imageUrls = listOf("images/1.jpg"),
        composition = "100% cotton",
        careInstructions = "Machine wash cold",
        features = listOf(ProductFeature("Fit", "Relaxed")),
        reviews = listOf(ProductReview("Oleh", 5, "Great quality"))
    )
    val sampleCartItems = listOf(CartItem(product = sampleProduct, quantity = 2))
    TShopAppTheme {
        CartScreen(
            cartItems = sampleCartItems,
            totalPrice = 2400.0,
            formatPrice = { price -> "${price.toInt()} ₴" },
            imagesBaseUrl = "",
            onRemoveClick = {},
            onIncrement = {},
            onDecrement = {}
        )
    }
}