package com.shop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.ui.components.CartItemRow
import com.shop.app.ui.theme.TShopAppTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    totalPrice: Double,
    modifier: Modifier = Modifier,
    onRemoveClick: (String) -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onPlaceOrder: () -> Unit,
    isPlacingOrder: Boolean,
    orderError: String?,
) {
    if (cartItems.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Cart is empty")
                if (orderError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = orderError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    } else {
        // Configure Ukrainian locale for currency formatting
        val currencyFormat = remember {
            NumberFormat.getCurrencyInstance(Locale("uk", "UA")).apply {
                maximumFractionDigits = 0
            }
        }

        Column(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemRow(
                        cartItem = cartItem,
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
                    text = "Total: ${currencyFormat.format(totalPrice)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onPlaceOrder,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPlacingOrder
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isPlacingOrder) "Placing order..." else "Place Order")
                }
                if (orderError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = orderError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
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
        gender = "unisex",
        name = "Sample Product",
        description = "Description",
        imagePath = "images/1.jpg",
        price = 1200.0,
        priceString = "1 200 ₴",
        isBestseller = true
    )
    val sampleCartItems = listOf(CartItem(product = sampleProduct, quantity = 2))
    TShopAppTheme {
        CartScreen(
            cartItems = sampleCartItems,
            totalPrice = 2400.0,
            onRemoveClick = {},
            onIncrement = {},
            onDecrement = {},
            onPlaceOrder = {},
            isPlacingOrder = false,
            orderError = null
        )
    }
}